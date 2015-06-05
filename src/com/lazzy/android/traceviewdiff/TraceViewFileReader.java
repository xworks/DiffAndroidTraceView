/**
 * Parse traceview file
 */

package com.lazzy.android.traceviewdiff;
/**
 * @author Li  jzqlin@gmail.com
 *
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.BufferUnderflowException;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TraceViewFileReader {
    private static final int TRACE_MAGIC = 0x574f4c53;

    private static final int METHOD_TRACE_ENTER = 0x00; // method entry
    private static final int METHOD_TRACE_EXIT = 0x01; // method exit
    private static final int METHOD_TRACE_UNROLL = 0x02; // method exited by exception unrolling

    // When in dual clock mode, we report that a context switch has occurred
    // when skew between the real time and thread cpu clocks is more than this
    // many microseconds.
    private static final long MIN_CONTEXT_SWITCH_TIME_USEC = 100;

    private enum ClockSource {
        THREAD_CPU, WALL, DUAL,
    };

    private int mVersionNumber;    
    private String mTraceFileName;
	private long mTotalCpuTime;
    private long mTotalRealTime;
    private int mRecordSize;
    private ClockSource mClockSource;
    
    private HashMap<Integer, MethodData> mMethodData = new HashMap<Integer, MethodData>();
    private HashMap<Integer, ThreadData> mThreadData = new HashMap<Integer, ThreadData>();
    
    // A regex for matching the thread "id name" lines in the .key file
    private static final Pattern mIdNamePattern = Pattern.compile("(\\d+)\t(.*)");  //$NON-NLS-1$

    public TraceViewFileReader(String traceFileName) throws IOException {
        mTraceFileName = traceFileName;                
        generateTrees();
    }

    void generateTrees() throws IOException {
        long offset = parseKeys();
        parseData(offset);
    }

    private MappedByteBuffer mapFile(String filename, long offset) throws IOException {
        MappedByteBuffer buffer = null;
        FileInputStream dataFile = new FileInputStream(filename);
        File file = new File(filename);
        FileChannel fc = dataFile.getChannel();
        buffer = fc.map(FileChannel.MapMode.READ_ONLY, offset, file.length() - offset);
        buffer.order(ByteOrder.LITTLE_ENDIAN);

        return buffer;
    }

    private void readDataFileHeader(MappedByteBuffer buffer) {
        int magic = buffer.getInt();
        if (magic != TRACE_MAGIC) {
            System.err.printf(
                    "Error: magic number mismatch; got 0x%x, expected 0x%x\n",
                    magic, TRACE_MAGIC);
            throw new RuntimeException();
        }

        // read version
        int version = buffer.getShort();
        if (version != mVersionNumber) {
            System.err.printf(
                    "Error: version number mismatch; got %d in data header but %d in options\n",
                    version, mVersionNumber);
            throw new RuntimeException();
        }
        if (version < 1 || version > 3) {
            System.err.printf(
                    "Error: unsupported trace version number %d.  "
                    + "Please use a newer version of TraceView to read this file.", version);
            throw new RuntimeException();
        }

        // read offset
        int offsetToData = buffer.getShort() - 16;

        // read startWhen
        buffer.getLong();

        // read record size
        if (version == 1) {
            mRecordSize = 9;
        } else if (version == 2) {
            mRecordSize = 10;
        } else {
            mRecordSize = buffer.getShort();
            offsetToData -= 2;
        }

        // Skip over offsetToData bytes
        while (offsetToData-- > 0) {
            buffer.get();
        }
    }

    private void parseData(long offset) throws IOException {
        MappedByteBuffer buffer = mapFile(mTraceFileName, offset);
        readDataFileHeader(buffer);

        final boolean haveThreadClock = mClockSource != ClockSource.WALL;
        final boolean haveGlobalClock = mClockSource != ClockSource.THREAD_CPU;

        // Parse all call records to obtain elapsed time information.
        for (;;) {
            int threadId;
            int methodId;
            long threadTime, globalTime;
            try {
                int recordSize = mRecordSize;

                if (mVersionNumber == 1) {
                    threadId = buffer.get();
                    recordSize -= 1;
                } else {
                    threadId = buffer.getShort();
                    recordSize -= 2;
                }

                methodId = buffer.getInt();
                recordSize -= 4;

                switch (mClockSource) {
                    case WALL:
                        threadTime = 0;
                        globalTime = buffer.getInt();
                        recordSize -= 4;
                        break;
                    case DUAL:
                        threadTime = buffer.getInt();
                        globalTime = buffer.getInt();
                        recordSize -= 8;
                        break;
                    default:
                    case THREAD_CPU:
                        threadTime = buffer.getInt();
                        globalTime = 0;
                        recordSize -= 4;
                        break;
                }

                while (recordSize-- > 0) {
                    buffer.get();
                }
            } catch (BufferUnderflowException ex) {
                break;
            }

			int methodAction = methodId & 0x03;
			methodId = methodId & ~0x03;
			switch (methodAction) 
			{
				case METHOD_TRACE_ENTER: 
					{
						System.out.printf("threadId:" + threadId + ",methodId :" + methodId + ",action METHOD_TRACE_ENTER,globalTime:"
								+ globalTime + ",threadTime:" + threadTime + "\n");			
						break;
					}
				case METHOD_TRACE_EXIT:
				case METHOD_TRACE_UNROLL: 
					{
						System.out.printf("threadId:" + threadId + ",methodId :" + methodId + ",action METHOD_TRACE_EXIT/UNROLL,globalTime:"
								+ globalTime + ",threadTime:" + threadTime + "\n");			

						break;
					}
				default:
					throw new RuntimeException("Unrecognized method action: " + methodAction);
			}
        }

   }

    static final int PARSE_VERSION = 0;
    static final int PARSE_THREADS = 1;
    static final int PARSE_METHODS = 2;
    static final int PARSE_OPTIONS = 4;

    long parseKeys() throws IOException {
        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(
                    new FileInputStream(mTraceFileName), "US-ASCII"));
        } catch (FileNotFoundException ex) {
            System.err.println(ex.getMessage());
        }

        long offset = 0;
        int mode = PARSE_VERSION;
        String line = null;
        while (true) {
            line = in.readLine();
            if (line == null) {
                throw new IOException("Key section does not have an *end marker");
            }

            // Calculate how much we have read from the file so far.  The
            // extra byte is for the line ending not included by readLine().
            offset += line.length() + 1;
            if (line.startsWith("*")) {
                if (line.equals("*version")) {
                    mode = PARSE_VERSION;
                    continue;
                }
                if (line.equals("*threads")) {
                    mode = PARSE_THREADS;
                    continue;
                }
                if (line.equals("*methods")) {
                    mode = PARSE_METHODS;
                    continue;
                }
                if (line.equals("*end")) {
                    break;
                }
            }
            switch (mode) {
            case PARSE_VERSION:
                mVersionNumber = Integer.decode(line);
                mode = PARSE_OPTIONS;
                break;
            case PARSE_THREADS:
                parseThread(line);
                break;
            case PARSE_METHODS:
                parseMethod(line);
                break;
            case PARSE_OPTIONS:
                parseOption(line);
                break;
            }
        }

        if (mClockSource == null) {
            mClockSource = ClockSource.THREAD_CPU;
        }
        return offset;
    }

    void parseOption(String line) {
        String[] tokens = line.split("=");
        if (tokens.length == 2) {
            String key = tokens[0];
            String value = tokens[1];

            if (key.equals("clock")) {
                if (value.equals("thread-cpu")) {
                    mClockSource = ClockSource.THREAD_CPU;
                } else if (value.equals("wall")) {
                    mClockSource = ClockSource.WALL;
                } else if (value.equals("dual")) {
                    mClockSource = ClockSource.DUAL;
                }
            }
        }
    }

    void parseThread(String line) {
        String idStr = null;
        String name = null;
        Matcher matcher = mIdNamePattern.matcher(line);
        if (matcher.find()) {
            idStr = matcher.group(1);
            name = matcher.group(2);
        }
        if (idStr == null) return;
        if (name == null) name = "(unknown)";

        int id = Integer.decode(idStr);
        
        //build thread data table
        ThreadData threadData = new ThreadData();
        threadData.setThreadId(id);
        threadData.setThreadName(name);
        
        mThreadData.put(Integer.valueOf(id), threadData);
    }

    void parseMethod(String line) {
        String[] tokens = line.split("\t");
        int id = Long.decode(tokens[0]).intValue();
        String className = tokens[1];
        String methodName = null;
        String signature = null;
        String pathname = null;
        int lineNumber = -1;
        if (tokens.length == 6) {
            methodName = tokens[2];
            signature = tokens[3];
            pathname = tokens[4];
            lineNumber = Integer.decode(tokens[5]);
            pathname = constructPathname(className, pathname);
        } else if (tokens.length > 2) {
            if (tokens[3].startsWith("(")) {
                methodName = tokens[2];
                signature = tokens[3];
            } else {
                pathname = tokens[2];
                lineNumber = Integer.decode(tokens[3]);
            }
        }
        
        //build method table
        MethodData methodData = new MethodData();
        methodData.setMethodId(id);
        methodData.setMethodName(methodName);
        methodData.setClassName(className);
        methodData.setMethodSignature(signature);
        methodData.setPathName(pathname);
        methodData.setLineNo(lineNumber);
        
        if (mMethodData.containsKey(Integer.valueOf(id))) {
        	System.err.printf("duplicated method id found!");
        	return ;
        }

        mMethodData.put(id, methodData);
    }

    private String constructPathname(String className, String pathname) {
        int index = className.lastIndexOf('/');
        if (index > 0 && index < className.length() - 1
                && pathname.endsWith(".java"))
            pathname = className.substring(0, index + 1) + pathname;
        return pathname;
    }
}

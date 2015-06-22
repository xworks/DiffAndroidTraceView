/**
 * Main entry of AndroidTraceViewDiff tool
 */
package com.lazzy.android.traceviewdiff;

import java.util.HashMap;

/**
 * @author Li  jzqlin@gmail.com
 *
 */
public class TraceViewDiff {
	
	   public static void main(String[] args) {
	        
	        String filePath1 = null;
	        String filePath2 = null;

	        if (args.length != 0) {
	            filePath1 = args[0];
	            filePath2 = args[1];
	        }
	        else {
	            System.out.printf("Usage: java TraceMain old_trace_file, new_trace_file\n");
	            return ;
	        }

	        try {
	        	TraceViewFileReader reader1 = new TraceViewFileReader(filePath1);
	            reader1.parse();
	            
	            TraceViewFileReader reader2 = new TraceViewFileReader(filePath2);
	            reader2.parse();
	      
	            //compare and output the diff result
	            diff(reader1, reader2);
	      
	        }
	        catch(Exception e) {
	            e.printStackTrace();
	            return ;
	        }
	   }
	   
	   public static void diff(TraceViewFileReader oldFileReader, TraceViewFileReader newFileReader) {
		   HashMap<Integer, MethodData> oldMethodMap = oldFileReader.getMethodMap();
		   HashMap<Integer, MethodData> newMethodMap = newFileReader.getMethodMap();
		   
		   for (MethodData oldMethod : oldMethodMap.values()) {
			   //System.out.printf("%s.%s\n", oldMethod.getClassName(), oldMethod.getMethodName());
			   for (MethodData newMethod : newMethodMap.values()) {
				   if (oldMethod.getMethodName() != null && newMethod.getMethodName() != null
						   && oldMethod.getClassName() != null && newMethod.getClassName() != null
						   && oldMethod.getSignature() != null && newMethod.getSignature() != null) {
					   if (oldMethod.getMethodName().equals(newMethod.getMethodName())
							   && oldMethod.getClassName().equals(newMethod.getClassName())
							   && oldMethod.getSignature().equals(newMethod.getSignature())) {
						   if (newMethod.getTopInclusiveRealTime() - oldMethod.getTopInclusiveRealTime() > 100*1000) {
							   System.out.printf("suspect method: %s.%s, old cost:%d, new cost:%d, %d longer\n",
									   newMethod.getClassName(), newMethod.getMethodName(), oldMethod.getTopInclusiveRealTime(),
									   newMethod.getTopInclusiveRealTime(), newMethod.getTopInclusiveRealTime() - oldMethod.getTopInclusiveRealTime());
						   }
					   }
					   
				   }

			   }
		   }

	   }


}

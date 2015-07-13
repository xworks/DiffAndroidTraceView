package com.lazzy.android.difftraceview;

public class ConsoleMain {

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
	        	DiffTraceView traceViewDiff = new DiffTraceView();
	        	traceViewDiff.process(filePath1, filePath2);
	        	
	        	traceViewDiff.consolePrint();
	        }
	        catch(Exception e) {
	            e.printStackTrace();
	            return ;
	        }
	   }
}

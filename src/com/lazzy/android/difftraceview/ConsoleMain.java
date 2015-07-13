package com.lazzy.android.difftraceview;

public class ConsoleMain {

	   public static void main(String[] args) {
	        
	        String filePath1 = null;
	        String filePath2 = null;
	        String filter = "";

	        if (args.length != 0) {
	            filePath1 = args[0];
	            filePath2 = args[1];
	            if (args.length > 2) {
	            	filter = args[2];
	            }
	            else {
	            	filter = "tencent"; //default filter string
	            }
	        }
	        else {
	            System.out.printf("Usage: java -jar diff_traceview.jar old_trace_file new_trace_file filter_string\n");
	            return ;
	        }

	        try {
	        	DiffTraceView traceViewDiff = new DiffTraceView();
	        	traceViewDiff.process(filePath1, filePath2);
	        	
	        	traceViewDiff.consolePrint(filter);
	        }
	        catch(Exception e) {
	            e.printStackTrace();
	            return ;
	        }
	   }
}

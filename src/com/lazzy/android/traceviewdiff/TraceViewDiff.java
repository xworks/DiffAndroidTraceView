package com.lazzy.android.traceviewdiff;

public class TraceViewDiff {
	
	   public static void main(String[] args) {
	        
	        String filePath = null;

	        if (args.length != 0) {
	            filePath = args[0];
	        }
	        else {
	            System.out.printf("Usage: java TraceMain trace_file_path\n");
	            return ;
	        }

	        try {
	        	TraceViewFileReader reader = null;
	            reader = new TraceViewFileReader(filePath);
	        }
	        catch(Exception e) {
	            e.printStackTrace();
	            return ;
	        }
	    }


}

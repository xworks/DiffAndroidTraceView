/**
 * 
 */
package com.lazzy.android.traceviewdiff;

/**
 * @author Li jzqlin@gmail.com
 *
 */
public class CallTree {
	private CallData mRoot;
	private CallData mNodeCursor = null;
	
	public CallTree() {
		mRoot = new CallData(null, null);
		mNodeCursor = mRoot;
	}
	
	public void callEnter(MethodData methodData, Integer threadId, long realTime, long CPUTime) {
		CallData callData = new CallData(methodData, mNodeCursor);
		callData.setRealTimeEnter(realTime);
		callData.setCPUTimeEnter(CPUTime);
	}
	
	public void callExitOrUnroll() {
		
	}
	
	
}

/**
 * Call data for each method call.
 * A method can be called many times.
 */
package com.lazzy.android.traceviewdiff;

import java.util.ArrayList;

/**
 * @author Li  jzqlin@gmail.com
 *
 */
public class CallData {
	private MethodData mMethodData;
	private CallData mParent;
	
	private long mRealTimeEnter;
	private long mRealTimeExit;
	private long mCpuTimeEnter;
	private long mCpuTimeExit;
	private boolean mIsRecursive = false;

	public CallData(MethodData methodData, CallData parent) {
		mMethodData = methodData;
		mParent = parent;
	}
	
	/*
	 * As a call
	 */
	
	public MethodData getMethodData() { return mMethodData; }
	
	public void setRealTimeEnter(long realTime) { mRealTimeEnter = realTime; }
	public void setRealTimeExit(long realTime) { mRealTimeExit = realTime; }

	public void setCpuTimeEnter(long CpuTime) { mCpuTimeEnter = CpuTime; }
	public void setCpuTimeExit(long CpuTime) { mCpuTimeExit = CpuTime; }
		
	public void setIsRecursive(boolean isRecursive)  { mIsRecursive = isRecursive; }
	
	//finish a call
	public void finish() {
		long elapsedRealTime = mRealTimeExit - mRealTimeEnter;
		long elapsedCpuTime = mCpuTimeExit - mCpuTimeEnter;
        
        if (mMethodData != null) {
        	mMethodData.updateCallFinish(mIsRecursive, elapsedRealTime, elapsedCpuTime);
        }
	}
	
	/*
	 * As a tree node
	 */
	public CallData getParent() { return mParent; }
}

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
	private ArrayList<CallData> mChildrens;
	
	private long mElapsedRealTime;
	private long mElapsedCPUTime;
	private long mRealTimeEnter;
	private long mRealTimeExitOrUnroll;
	private long mCPUTimeEnter;
	private long mCPUTimeExitOrUnroll;

	public CallData(MethodData methodData, CallData parent) {
		mMethodData = methodData;
		mParent = parent;
		mChildrens = new ArrayList<CallData>();
		mRealTimeEnter = 0;
		mRealTimeExitOrUnroll = 0;
		mCPUTimeEnter = 0;
		mCPUTimeExitOrUnroll = 0;
		mElapsedRealTime = 0;
		mElapsedCPUTime = 0;
	}
	
	/*
	 * As a call
	 */
	
	public MethodData getMethodData() { return mMethodData; }
	
	public void setRealTimeEnter(long realTime) { mRealTimeEnter = realTime; }
	public void setRealTimeExitOrUnroll(long realTime) { mRealTimeExitOrUnroll = realTime; }

	public void setCPUTimeEnter(long CPUTime) { mCPUTimeEnter = CPUTime; }
	public void setCPUTimeExitOrUnroll(long CPUTime) { mCPUTimeExitOrUnroll = CPUTime; }

	public long getElapsedRealTime() { 
		mElapsedRealTime = mRealTimeExitOrUnroll - mRealTimeEnter;
		return mElapsedRealTime; 
	}
	
	public long getElpasedCPUTime() { 
		mElapsedCPUTime = mCPUTimeExitOrUnroll - mCPUTimeEnter;
		return mElapsedCPUTime; 
	}
	
	
	/*
	 * As a tree node
	 */

	public CallData getParent() { return mParent; }
	
	public ArrayList<CallData> getChildrens() { return mChildrens; }
	
	public void addChild(CallData child) {
		mChildrens.add(child);
	}


}

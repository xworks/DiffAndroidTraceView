package com.lazzy.android.traceviewdiff;

import java.util.ArrayList;
import java.util.HashMap;

public class ThreadData {
	private Integer mThreadId;
	private String mThreadName;
	
	private CallData mRootCall;
	private long mRealTimeStart = 0;
	private long mRealTimeEnd = 0;
	private long mCpuTimeStart = 0;
	private long mCpuTimeEnd = 0;
	
	private boolean mHasSetStartTime = false;
	
	private ArrayList<CallData> mCallStack;
	private HashMap<MethodData, Integer> mMethodCounts;
	
	public ThreadData(Integer threadId, String threadName, MethodData rootMethod) {
		mThreadId = threadId;
		mThreadName = threadName;
	
		mCallStack = new ArrayList<CallData>();
		mMethodCounts = new HashMap<MethodData, Integer>();

		mRootCall = new CallData(rootMethod, null);
		mCallStack.add(mRootCall);
	}
	
	public Integer getThreadId() { return mThreadId; }
	public String getThreadName() { return mThreadName; }
	public void setRealTimeStart(long realTimeStart) { 
		this.mRealTimeStart = realTimeStart; 
		mHasSetStartTime = true;
	}
	public void setRealTimeEnd(long realTimeEnd) { this.mRealTimeEnd = realTimeEnd; }
	public void setCpuTimeStart(long cpuTimeStart) { this.mCpuTimeStart = cpuTimeStart; }
	public void setCpuTimeEnd(long cpuTimeEnd) { this.mCpuTimeEnd = cpuTimeEnd; }
	public long getCpuTimeStart() { return mCpuTimeStart; }
	public long getCpuTimeEnd() { return this.mCpuTimeEnd; }
	public long getRealTimeEnd() { return this.mRealTimeEnd; }
	public long getRealTimeStart() { return mRealTimeStart; }
	public boolean hasSetStartTime() { return mHasSetStartTime; }
	public CallData getRootCall() { return mRootCall; }
	
	public void callEnter(MethodData methodData, long CpuTime, long realTime)
	{
		//enter a new call
		//the call on the top of the stack is it's parent
		CallData newParent = callStackTop();
		CallData newCall = new CallData(methodData, newParent);
		newCall.setCpuTimeEnter(CpuTime);
		newCall.setRealTimeEnter(realTime);
		mCallStack.add(newCall);
	
		//calculate the method's calling counts
		Integer counts = mMethodCounts.get(methodData);
		if (counts == null) {
			counts = 0;
		}
		else if (counts > 0){
			//the method is recursive calling
			newCall.setIsRecursive(true);
		}
		mMethodCounts.put(methodData, counts + 1);
	}
	
	
	public void callExit(MethodData methodData, long CpuTime, long realTime)
	{
		//check if the exit method is on the top of the stack
		CallData call = callStackTop();
		if (call.getParent() == null) {
			return ;
		}
		
		if (call.getMethodData() != methodData) {
			String error = "Method exit (" + methodData.getMethodName()
                    + ") does not match current method (" + call.getMethodData().getMethodName()
                    + ")";
            throw new RuntimeException(error);
		}
		
		call.setCpuTimeExit(CpuTime);
		call.setRealTimeExit(realTime);
		call.finish();
		mCallStack.remove(mCallStack.size() - 1);
		
        //calculate the method's calling counts
		Integer counts = mMethodCounts.get(methodData);
		if (counts != null) {
			if (counts == 1) {
				mMethodCounts.remove(methodData);
			}
			else {
				mMethodCounts.put(methodData, counts -1);
			}
		}
	}
	
	public CallData callStackTop() {
		return mCallStack.get(mCallStack.size() - 1);
	}
	
	//thread finish
	public void finish() {
		//update root call time bounds
		mRootCall.setCpuTimeEnter(mCpuTimeStart);
		mRootCall.setCpuTimeExit(mCpuTimeEnd);
		mRootCall.setRealTimeEnter(mRealTimeStart);
		mRootCall.setRealTimeExit(mRealTimeEnd);
		
		//update all calls timing that did not receive an exit event
		for (CallData call : mCallStack) {
			call.setCpuTimeExit(mCpuTimeEnd);
			call.setRealTimeExit(mRealTimeEnd);
			call.finish();
		}
		
		mCallStack.clear();
		mMethodCounts.clear();
	}
	
	
	
}

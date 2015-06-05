/**
 * Method data container for each method.
 * A method can be called many times.
 */
package com.lazzy.android.traceviewdiff;

import java.util.ArrayList;

/**
 * @author Li  jzqlin@gmail.com
 *
 */
public class MethodData {

	private String mClassName;
	private int mLineNo;
	private String mPathName;
	private Integer mMethodId;
	private String mMethodName;
	private String mMethodSignature;

	private long mTotalElapsedRealTime;
	private long mTotalElapsedCPUTime;

	private ArrayList<CallData> mCallList;
	
	public MethodData() {
		mTotalElapsedRealTime = 0;
		mTotalElapsedCPUTime = 0;
		mCallList = new ArrayList<CallData>();
	}

	public String getClassName() { return mClassName; }

	public void setClassName(String className) { this.mClassName = className; }

	public int getLineNo() { return mLineNo; }

	public void setLineNo(int lineNo) { this.mLineNo = lineNo; }

	public String getPathName() { return mPathName; }

	public void setPathName(String pathName) { this.mPathName = pathName; }

	public Integer getMethodId() { return mMethodId; }

	public void setMethodId(Integer methodId) { this.mMethodId = methodId; }

	public String getMethodName() { return mMethodName; }

	public void setMethodName(String methodName) { this.mMethodName = methodName; }

	public String getMethodSignature() { return mMethodSignature; }

	public void setMethodSignature(String methodSignature) { this.mMethodSignature = methodSignature; }

	public long getTotalElapsedRealTime() { return mTotalElapsedRealTime; }

	public void addElapsedRealTime(long realTime) { mTotalElapsedRealTime += realTime; }

	public long getTotalElapsedCPUTime() { return mTotalElapsedCPUTime; }

	public void addElapsedCPUTime(long CPUTime) { mTotalElapsedCPUTime += CPUTime; }
	
	public ArrayList<CallData> callList() { return mCallList; }

}

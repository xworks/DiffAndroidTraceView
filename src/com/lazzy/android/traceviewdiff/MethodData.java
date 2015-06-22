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
	private String mSignature;

	private long mTopInclusiveRealTime; // real time spend on this method
	private long mTopInclusiveCpuTime;  // cpu time spend on this method
	
	private int mCallNum;
	private int mRecursiveCallNum;
	
	public MethodData() {
		mClassName = null;
		mPathName = null;
		mMethodName = null;
		mSignature = null;
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

	public String getSignature() { return mSignature; }

	public void setSignature(String methodSignature) { this.mSignature = methodSignature; }

	public long getTopInclusiveRealTime() { return mTopInclusiveRealTime; }
	public long getTopInclusiveCpuTime() { return mTopInclusiveCpuTime; }
	
	public void updateCallFinish(boolean isRecursive, long elapsedRealTime, long elapsedCpuTime) {
		mCallNum++;
		if (isRecursive) {
			mRecursiveCallNum++;
		}
		else {
			mTopInclusiveRealTime += elapsedRealTime;
			mTopInclusiveCpuTime += elapsedCpuTime;
		}
	}

	public int getCallNum() { return mCallNum; }
	
	public int getRecursiveCallTimes() { return mRecursiveCallNum; }
}

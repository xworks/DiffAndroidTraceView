package com.lazzy.android.difftraceview;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MethodCompareData {
	public long oldMethodId;
	public MethodData oldMethod;
	
	public long newMethodId;
	public MethodData newMethod;
	
	public long timeCostDiff;
	
	public static class SortByTimeCostDiff implements Comparator<MethodCompareData>{
		
		@Override
		public int compare(MethodCompareData o1, MethodCompareData o2) {
			if (o1.timeCostDiff > o2.timeCostDiff) {
				return -1;
			}
			else if (o1.timeCostDiff == o2.timeCostDiff) {
				return 0;
			}
			return 1;
		}
	}
	
}

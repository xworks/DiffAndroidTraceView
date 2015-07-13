/**
 * Main entry of AndroidTraceViewDiff tool
 */
package com.lazzy.android.difftraceview;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import javax.naming.ldap.SortControl;

/**
 * @author Li  jzqlin@gmail.com
 *
 */
public class DiffTraceView {
    public ArrayList<MethodData> newAddedMethods = new ArrayList<MethodData>();
    public ArrayList<MethodCompareData> methodCompares = new ArrayList<MethodCompareData>();

    public void process(String filePath1, String filePath2) {        

        try {
            TraceViewFileReader reader1 = new TraceViewFileReader(filePath1);
            reader1.parse();

            TraceViewFileReader reader2 = new TraceViewFileReader(filePath2);
            reader2.parse();

            //compare and output the diff result
            diff(reader1, reader2);

        }
        catch(Exception e) {
            e.printStackTrace();
            return ;
        }
    }


    public void diff(TraceViewFileReader oldFileReader, TraceViewFileReader newFileReader) {
        HashMap<Integer, MethodData> oldMethodMap = oldFileReader.getMethodMap();
        HashMap<Integer, MethodData> newMethodMap = newFileReader.getMethodMap();

        newAddedMethods.clear();
        methodCompares.clear();

        //find the new methods
        for (MethodData newMethod : newMethodMap.values()) {
            boolean found = false;
            for (MethodData oldMethod : oldMethodMap.values()) {
                if (isMethodEquals(oldMethod, newMethod)) {
                    found = true; 
                    break;	
                }
            }

            if (!found) {
                newAddedMethods.add(newMethod);
            }
        }

        //比较方法的耗时，并排序
        for (MethodData oldMethod : oldMethodMap.values()) {
            //System.out.printf("%s.%s\n", oldMethod.getClassName(), oldMethod.getMethodName());
            for (MethodData newMethod : newMethodMap.values()) {
                if (isMethodEquals(oldMethod, newMethod)) {
                    MethodCompareData methodCompare = new MethodCompareData();

                    methodCompare.oldMethodId = oldMethod.getMethodId();
                    methodCompare.oldMethod = oldMethod;
                    methodCompare.newMethodId = newMethod.getMethodId();
                    methodCompare.newMethod = newMethod;
                    methodCompare.timeCostDiff = newMethod.getTopInclusiveRealTime() - oldMethod.getTopInclusiveRealTime();

                    methodCompares.add(methodCompare);
                }
            }
        }

        Collections.sort(methodCompares, new MethodCompareData.SortByTimeCostDiff()); 
    }


    public boolean isMethodEquals(MethodData oldMethod, MethodData newMethod) {
        if (oldMethod.getMethodName() != null && newMethod.getMethodName() != null
                && oldMethod.getClassName() != null && newMethod.getClassName() != null
                && oldMethod.getSignature() != null && newMethod.getSignature() != null) {
            if (oldMethod.getMethodName().equals(newMethod.getMethodName())
                    && oldMethod.getClassName().equals(newMethod.getClassName())
                    && oldMethod.getSignature().equals(newMethod.getSignature())) {
                return true;
                    }
                }
        return false;
    }


    public void consolePrint(String filter) {
        System.out.printf("\n\n");
        System.out.printf("%-100s%-25s%-25s%-18s\n", "Method Name", "Inc RealTime(ms) File1", "Inc RealTime(ms) File2", "Diff(ms) File2-File1");
        System.out.printf("-------------------------------------------------------------------------------------------------------------------------------------------------------------------------\n");
        for (MethodCompareData methodCompare : methodCompares) {
            String name = methodCompare.oldMethod.getClassName() + "." + methodCompare.oldMethod.getMethodName() + "(..)";
            if (name.contains(filter) && methodCompare.timeCostDiff/1000 != 0) {
                System.out.printf("%-100s%-25d%-25d%-18d\n", name, methodCompare.oldMethod.getTopInclusiveRealTime()/1000, methodCompare.newMethod.getTopInclusiveRealTime()/1000,
                        methodCompare.timeCostDiff/1000);
            }
        }

    }



}

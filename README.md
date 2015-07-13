# DiffAndroidTraceView
Compare the same method's inclusive real time cost between tow Anroid TraceView file. You can use it for traceing the method's cpu performance between different versions.

## Useage: 
```java com.lazzy.android.difftraceview.ConsoleMain  traceview_file1  traceview_file2```

or
```java -jar diff_traceview.jar traceview_file1  traceview_file2```

## Output:

```
Method Name                                             Inc RealTime(ms) File1   Inc RealTime(ms) File2   Diff(ms) File2-File1
-----------------------------------------------------------------------------------------------------------------------------
com/lazzy/mobile/app/Job.run(..)                        5145                     6138                     992               
com/lazzy/feedback/eup/jni/a.run(..)                    1080                     1563                     482               
com/lazzy/feedback/proguard/l.c(..)                     888                      1358                     470               
com/lazzy/biz/webviewplugin/KeyInfo.getKeys(..)         2087                     2532                     444               
com/lazzy/smtt/utils/ReflectionUtils.invokeInstance(..) 4                        445                      440               
com/lazzy/biz/webviewplugin/KeyInfo$1.run(..)           2225                     2662                     437               
com/lazzy/smtt/sdk/WebSettings.setPluginsEnabled(..)    0                        436                      435               
com/lazzy/mobile/startup/step/Step.step(..)             1609                     1774                     165               
com/lazzy/mobile/startup/step/Step.run(..)              715                      851                      135               
com/lazzy/biz/AuthorizeConfig$1.run(..)                 137                      267                      130               
com/lazzy/mxx/shared_file_accessor/n.getLong(..)        137                      267                      130               
com/lazzy/mobile/app/BrowserAppInterface.getVkey(..)    97                       211                      113               
com/lazzy/mxx/shared_file_accessor/f.a(..)              579                      688                      108               
com/lazzy/mobile/startup/step/NewRuntime.doStep(..)     282                      370                      88                
com/lazzy/open/base/LogUtility.log(..)                  6                        92                       85                
com/lazzy/open/base/LogUtility.i(..)                    6                        92                       85                
com/lazzy/qphone/base/util/QLog.isColorLevel(..)        13                       88                       75                
com/lazzy/smtt/sdk/WebSettings.setPluginState(..)       0                        63                       62                
.......
```
  

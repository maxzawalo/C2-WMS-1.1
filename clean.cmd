set PATH=%PATH%;D:\CI_CD\apache-maven-3.6.3\bin
set mvn_home=D:\CI_CD\.m2\repository
set JAVA_HOME=D:\Program Files\Java\jdk1.8.0_131

rmdir %mvn_home%\maxzawalo\c2\ /S /Q
rmdir PRODUCTION /S /Q

cls

call mvn -f maxzawalo.c2.base.utils/pom.xml -Dmaven.repo.local=%mvn_home% clean
call mvn -f maxzawalo.c2.base.os/pom.xml -Dmaven.repo.local=%mvn_home% clean 
call mvn -f maxzawalo.c2.base.crypto/pom.xml -Dmaven.repo.local=%mvn_home% clean 
call mvn -f maxzawalo.c2.free.hardware/pom.xml -Dmaven.repo.local=%mvn_home% clean

call mvn -f maxzawalo.c2.free.ui.pc.resource/pom.xml -Dmaven.repo.local=%mvn_home% clean 

call mvn -f maxzawalo.c2.free.bo/pom.xml -Dmaven.repo.local=%mvn_home% clean 
call mvn -f maxzawalo.c2.free.accounting/pom.xml -Dmaven.repo.local=%mvn_home% clean 
call mvn -f maxzawalo.c2.free.cache/pom.xml -Dmaven.repo.local=%mvn_home% clean 
call mvn -f maxzawalo.c2.free.search/pom.xml -Dmaven.repo.local=%mvn_home% clean 

call mvn -f maxzawalo.c2.base.data/pom.xml -Dmaven.repo.local=%mvn_home% clean 
call mvn -f maxzawalo.c2.free.data/pom.xml -Dmaven.repo.local=%mvn_home% clean 
call mvn -f maxzawalo.c2.free.accounting.data/pom.xml -Dmaven.repo.local=%mvn_home% clean 
call mvn -f maxzawalo.c2.free.data.json/pom.xml -Dmaven.repo.local=%mvn_home% clean 

call mvn -f maxzawalo.c2.base.ui.pc/pom.xml -Dmaven.repo.local=%mvn_home% clean 

call mvn -f maxzawalo.c2.free.reporter/pom.xml -Dmaven.repo.local=%mvn_home% clean 

call mvn -f maxzawalo.c2.full.data.load_bank/pom.xml -Dmaven.repo.local=%mvn_home% clean 

call mvn -f maxzawalo.c2.full.bo/pom.xml -Dmaven.repo.local=%mvn_home% clean 
call mvn -f maxzawalo.c2.full.data/pom.xml -Dmaven.repo.local=%mvn_home% clean 
call mvn -f maxzawalo.c2.full.data.json/pom.xml -Dmaven.repo.local=%mvn_home% clean 
call mvn -f maxzawalo.c2.full.synchronization/pom.xml -Dmaven.repo.local=%mvn_home% clean 

call mvn -f maxzawalo.c2.full.analitics/pom.xml -Dmaven.repo.local=%mvn_home% clean 

call mvn -f maxzawalo.c2.full.reporter/pom.xml -Dmaven.repo.local=%mvn_home% clean 
call mvn -f maxzawalo.c2.base.www/pom.xml -Dmaven.repo.local=%mvn_home% clean 

call mvn -f maxzawalo.c2.full.report/pom.xml -Dmaven.repo.local=%mvn_home% clean 

call mvn -f maxzawalo.c2.full.ai.ml/pom.xml -Dmaven.repo.local=%mvn_home% clean 
call mvn -f maxzawalo.c2.full.el_doc/pom.xml -Dmaven.repo.local=%mvn_home% clean 
call mvn -f maxzawalo.c2.full.hardware/pom.xml -Dmaven.repo.local=%mvn_home% clean 

call mvn -f "C2 Free/pom.xml" -Dmaven.repo.local=%mvn_home% clean 
call mvn -f "C2 Full/pom.xml" -Dmaven.repo.local=%mvn_home% clean

pause
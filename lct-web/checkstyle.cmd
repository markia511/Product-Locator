set old_patch=%path%
set path=D:\apache-maven-3.2.1\bin;%path%

del /S /Q build-tools\target\*.*

call mvn jxr:aggregate -f build-tools\pom.xml
call mvn site -f build-tools\pom.xml -X

set path=%old_path%

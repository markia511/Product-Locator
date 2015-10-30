
if (%1)==() goto error

set old_patch=%path%
set path=\apache-maven-3.2.3\bin;%path%

del /S /Q lct-common\target\*.*
del /S /Q lct-ws\target\*.*
del /S /Q lct-web\target\*.*
del /S /Q lct-web-gfruit\target\*.*
del /S /Q lct-ear\target\*.*
del /S /Q build-tools\target\*.*

call get_version_number.cmd
call mvn -DnewVersion=2.0.0.%FVN% versions:set versions:update-child-modules
call mvn -P %1 package
rem move lct-ear\target\lct-*.ear .
call mvn versions:revert

set path=%old_path%

goto end

:error
@echo Please do not use this command file directly. Use the appropriate file: build_local_tc.cmd, build_local_was.cmd, build_prod.cmd, build_test.cmd

:end

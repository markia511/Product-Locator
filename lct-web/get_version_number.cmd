@@for /f "tokens=1,2 delims=="  %%A in (LCT-web\src\main\resources\messages.properties) do @@if "%%A"=="build.version" set FVN=%%B
@@for /f "tokens=3 delims=." %%A in ('echo %FVN%') do set FVN=%%A
@@set FVN=%FVN:~1%

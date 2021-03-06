@REM
@REM  Copyright 2004 The Apache Software Foundation
@REM
@REM  Licensed under the Apache License, Version 2.0 (the "License");
@REM  you may not use this file except in compliance with the License.
@REM  You may obtain a copy of the License at
@REM
@REM      http://www.apache.org/licenses/LICENSE-2.0
@REM
@REM  Unless required by applicable law or agreed to in writing, software
@REM  distributed under the License is distributed on an "AS IS" BASIS,
@REM  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
@REM  See the License for the specific language governing permissions and
@REM  limitations under the License.
@REM

@rem Instance to Schema tool
@rem
@rem Builds xsd files from xml instance files.

@echo off

setlocal
if "%XMLBEANS_LIB%" EQU "" call "%~dp0_setlib"

set cp=
set cp=%cp%;%XMLBEANS_LIB%\*
set cp=%cp%;%XMLBEANS_LIB%\jsr173_1.0_api.jar
set cp=%cp%;%XMLBEANS_LIB%\resolver.jar

java -classpath "%cp%" org.apache.xmlbeans.impl.inst2xsd.Inst2Xsd %*

:done

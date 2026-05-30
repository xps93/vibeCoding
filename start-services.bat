@echo off
echo Starting all services...

REM Backend
set JAVA_HOME=D:\devTool-2022-new\jdk
cd /d C:\Users\25894\IdeaProjects\spring-boot-admin
start "SpringBootAdmin" D:\devTool-2022-new\apache-maven-3.6.3\bin\mvn.cmd spring-boot:run

REM AI Client
cd /d C:\Users\25894\IdeaProjects\ds_ai_web
start "AI-Client" cmd /c "npm run dev"

REM Admin
cd /d C:\Users\25894\IdeaProjects\vue-admin
start "Admin" cmd /c "npm run dev"

echo All services started in separate windows.
echo Backend: http://localhost:8089
echo AI Client: http://localhost:5174
echo Admin: http://localhost:5173

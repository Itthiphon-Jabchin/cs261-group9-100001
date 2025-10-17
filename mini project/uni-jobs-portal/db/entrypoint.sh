#!/bin/bash
# db-init/entrypoint.sh

# 1. เริ่มการทำงานของ SQL Server ใน background
# เครื่องหมาย '&' คือการสั่งให้ทำงานเบื้องหลัง
/opt/mssql/bin/sqlservr &

# 2. รันสคริปต์ setup.sql ของเรา
#    -i คือการระบุ path ของไฟล์ .sql ที่จะรัน
/opt/mssql-tools18/bin/sqlcmd -S localhost -U sa -P "$SA_PASSWORD" -i /usr/src/app/setup.sql

# 3. รอให้ SQL Server process หลักทำงานต่อไปเรื่อยๆ
#    เพื่อให้ container ไม่ปิดตัวเองลง
wait
# QR Code Attendance - Render Deployment

## Prerequisites
- Render account
- GitHub repository with this code

## Deployment Steps

1. **Push to GitHub**:
   ```bash
   git init
   git add .
   git commit -m "Initial commit"
   git remote add origin https://github.com/yourusername/your-repo.git
   git push -u origin main
   ```

2. **Deploy on Render**:
   - Go to [Render Dashboard](https://dashboard.render.com)
   - Click "New" → "Web Service"
   - Connect your GitHub repo
   - Set build command: (leave empty for Docker)
   - Set start command: (leave empty for Docker)
   - Add environment variable: `DATABASE_URL` with your Neon connection string
   - Deploy

3. **Database Setup**:
   - Connect to your Neon database
   - Run the `schema.sql` script

4. **Update Android App**:
   - Change `BASE_URL` in `MainActivity.java` to your Render service URL + `/check_login.php`
   - Change `BASE_URL` in `ScanQRCodeActivity.java` to your Render service URL + `/insert_attendance.php`

## Notes
- The app uses Docker for deployment
- PostgreSQL connection is handled via PDO
- CORS headers are set for Android app communication
- HTTPS is automatically enabled on Render
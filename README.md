# zajavka
To run the application using Docker, use the following commands:

Building the image:
docker build -t projektzajavka2:latest .

Running the application:
docker run -p 8085:8085 projektzajavka2:latest

After starting the application, you can access it in your browser at:
    http://localhost:8085/
    http://localhost:8085/security/login

User with Administrator privileges:
    Login: testuser
    Password: password

The application uses an H2 database.


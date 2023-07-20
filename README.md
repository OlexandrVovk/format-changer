# Word to PDF Converter

The "Word to PDF Converter" project is a web application that provides the functionality to transform Word documents (docx) into PDF format. This conversion is achieved using the Gotenberg tool, which offers an API for document conversion.

### Project Functionality:
- Upload Word (docx) files to the web application.
- Convert the uploaded documents to PDF format.
- Download the resulting PDF files to the user's device.

### Gotenberg Documentation:
For more detailed information about the functionality and configuration of the Gotenberg API, you can refer to the documentation available at: [Gotenberg Documentation](https://gotenberg.dev/docs/get-started/live-demo)

### How to Run the Project:
To run the project, follow these steps:

1. Clone the project repository from GitHub.
2. Install Docker and Docker Compose on your system if you haven't already.
3. Open a terminal/command prompt and navigate to the project repository folder.
4. Execute the command `docker-compose up` to launch the project.

After completing these steps, your web application will be accessible at the `http://localhost:9091`.
In addition you can use Gotenberg API at the `http://localhost:3000`.   

You are now ready to use the web application for converting Word documents to PDF.

**Note:** Before running the project, ensure that ports 9091 and 3000 on your computer are not occupied by another process.
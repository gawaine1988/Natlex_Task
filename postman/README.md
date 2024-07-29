# Postman Collection for Natlex Task Backend

This directory contains the Postman collection to facilitate testing of the Natlex Task Backend API endpoints.

## Files

- `Natlex_Task.postman_collection.json`: The Postman collection file.
- `Natlex.postman_environment.json`: The Postman environment file.

## Importing the Postman Collection

1. Open Postman.
2. Click on `Import`.
3. Select the `Natlex_Task.postman_collection.json` file from this directory.
4. Select the `Natlex.postman_environment.json` file from this directory.
5. The collection and environment will be imported, and you can use them to test the API endpoints.

## Usage

1. **Basic Authorization**: Some endpoints require Basic Authorization. Configure the authorization in the Postman request by:
   - Going to the `Authorization` tab.
   - Selecting `Basic Auth` from the `Type` dropdown.
   - Entering the username and password.

2. **CSRF Protection**: For POST requests, you will need to obtain a CSRF token.
   - Make a GET request to the base URL to get the CSRF token.
   - Include the token in the headers of your POST request.

3. **Selecting the Environment**: Before using the collection, ensure that the correct environment is selected in Postman:
   - Choose `Natlex` from the environment dropdown in the top right corner of the Postman window.

4. **Endpoints**: The collection includes requests for all the API endpoints:
   - CRUD operations for Sections and GeologicalClasses.
   - Import and Export operations for XLS files.
   - Checking the status of import/export jobs.
   
5. **Importing a File**: To import a file using the `http://localhost:8080/import` endpoint:
   - Open Postman and select the POST request for the `/import` endpoint from the collection.
   - Go to the `Body` tab.
   - Select `form-data`.
   - In the `Key` field, enter `file` (this must match the `@RequestParam("file")` in your API).
   - In the `Value` field, click on the dropdown menu and select `File`.
   - Click on the `Select Files` button and choose the file you want to import from your local system.
   - Ensure the `Content-Type` header is set to `multipart/form-data`.
   - Click the `Send` button to upload the file.

## Contact

For any questions or feedback, please contact Peng Yan at gawaine1988@gmail.com.

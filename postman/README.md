# Postman Collection for Natlex Task Backend

This directory contains the Postman collection to facilitate testing of the Natlex Task Backend API endpoints.

## Files

- `Natlex_Task.postman_collection.json`: The Postman collection file.

## Importing the Postman Collection

1. Open Postman.
2. Click on `Import`.
3. Select the `Natlex_Task.postman_collection.json` file from this directory.
4. The collection will be imported and you can use it to test the API endpoints.

## Usage

1. **Basic Authorization**: Some endpoints require Basic Authorization. Configure the authorization in the Postman request by:
    - Going to the `Authorization` tab.
    - Selecting `Basic Auth` from the `Type` dropdown.
    - Entering the username and password.

2. **CSRF Protection**: For POST requests, you will need to obtain a CSRF token.
    - Make a GET request to the base URL to get the CSRF token.
    - Include the token in the headers of your POST request.

3. **Endpoints**: The collection includes requests for all the API endpoints:
    - CRUD operations for Sections and GeologicalClasses.
    - Import and Export operations for XLS files.
    - Checking the status of import/export jobs.

## Contact

For any questions or feedback, please contact Peng Yan at gawaine1988@gmail.com.

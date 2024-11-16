# Quiz Client-Server Application

A Java-based client-server quiz application that allows users to take quizzes through a network connection.

## Features

- Client-server architecture using TCP/IP sockets
- Multiple-choice quiz system
- UUID-based session management
- Real-time feedback on answers
- Score tracking
- Configurable server settings

## System Requirements

- Java Runtime Environment (JRE) 8 or higher
- Network connectivity between client and server

## Project Structure

- `QuizServer.java`: Main server implementation that handles client connections
- `QuizClient.java`: Client application for taking quizzes
- `QuizServerInstance.java`: Handles individual quiz sessions
- `QuizProtocol.java`: Defines the communication protocol between client and server
- `Question.java`: Question model class
- `ServiceResponse.java`: Response handling utility
- `CommonUtil.java`: Common utility functions

## Configuration

Server configuration is stored in `server_info.dat`:
# Quiz Client-Server Application

A Java-based client-server quiz application that allows users to take quizzes through a network connection.

## Features

- Client-server architecture using TCP/IP sockets
- Multiple-choice quiz system
- UUID-based session management
- Real-time feedback on answers
- Score tracking
- Configurable server settings

## System Requirements

- Java Runtime Environment (JRE) 8 or higher
- Network connectivity between client and server

## Project Structure

- `QuizServer.java`: Main server implementation that handles client connections
- `QuizClient.java`: Client application for taking quizzes
- `QuizServerInstance.java`: Handles individual quiz sessions
- `QuizProtocol.java`: Defines the communication protocol between client and server
- `Question.java`: Question model class
- `ServiceResponse.java`: Response handling utility
- `CommonUtil.java`: Common utility functions

## Configuration

Server configuration is stored in `server_info.dat`:
```text
host:127.0.0.1
port:1234
```

## Protocol Methods

The application supports the following quiz methods:
- START: Begin a new quiz session
- ASK_QUESTION: Request a new question
- SUBMIT_ANSWER: Submit an answer to a question
- GET_SCORE: Retrieve the final score
- END: End the quiz session
- GET_UUID: Get a unique session identifier
- RESPONSE: Server response message

## How to Run

1. Start the server:
```bash
java QuizServer
```


2. Start the client:
```bash
java QuizClient
```

## Quiz Flow

1. Client connects and receives a UUID
2. Client starts a quiz session
3. Client requests questions one by one
4. Client submits answers and receives feedback
5. Client can request final score
6. Client ends the session

## Error Handling

The application includes comprehensive error handling:
- Invalid method requests
- Network connectivity issues
- Invalid answer submissions
- Session management errors

## Status Codes

- 200 OK: Successful request
- 400 Bad Request: Invalid request parameters
- 500 Internal Server Error: Server-side issues

## Contributing

Feel free to submit issues and enhancement requests.

## License

This project is licensed under the MIT License - see the LICENSE file for details.
# simplemessagingapp [![Build Status](https://travis-ci.org/oche-jay/simplemessagingapp.svg?branch=master)](https://travis-ci.org/oche-jay/simplemessagingapp)

This simple messaging app functions as a Web Server that accepts JSON messages sent by HTTP post to a configurable port 
(default price: 8000).

### Build Instructions
To test and build this application:

    git clone https://github.com/oche-jay/simplemessagingapp && cd simplemessagingapp
    mvn test exec:java
    
### Messaging Interface
 The app accepts valid JSON messages sent via HTTP POST to the /sales endpoint, e.g http://localhost:8000/sales
 
 The message payloads have been are specified as follows:
 
 - Message Type 1: The details of 1 sale E.g apple at 10p
          
          {
             "product": "apple",
             "price": "0.10"
          }
 
 - Message Type 2: The details of a sale and the number of counts of
                   that sale. E.g 20 sales of apples at 10p each.
                   
           {
              "product": "apple",
              "price": "0.10",
              "count": 20
           }
                   
 - Message 3: The details of an adjustment operation to be
              applied to all stored sales of this product type. Operations can be ADD, SUBTRACT, or MULTIPLY 
              e.g the following message would instruct the application to add 20p to each sale of apples recorded.
              
            {
               "product": "apple",
               "price": "0.10",
               "instruction": "ADD"
            }  
 
 The app considers the "product" and "price" fields as required. Any messages sent without either of these
 field will be considered as invalid and a HTTP Status 400 will be returned by the server. Furthermore,
 the app expects the values for "price" and "count" to be non-zero and postive.
 
 ### Testing
A suite of data-driven unit tests can be found in  [src/test/java](/src/test/java/ConfigTest.java) folder.
Some of the tests do print sample message payloads to the console and be these can be used to test
the application manually (e.g. using postman or curl).

 ### Acceptance Criteria                             

  - [x] All sales must be recorded
         
             All valid messages are logged and added to internal data-structures
 
  - [x]  All messages must be processed  
             
              All incoming messages are validated, queued and then processed. For invalid messages, 
              a HTTP 400 Status(Bad Request) along with the reason/error message is returned to the user.          

  - [x] After every 10th message received your application should log a report detailing the number
                of sales of each product and their total value. 
                
                Done
                
  - [x] After 50 messages your application should log that it is pausing, stop accepting new messages and log a report of the adjustments that have been made to each sale type while the application was running.      
               
               The server prints the required report and then pauses for a configurable amount of 
               time (default value is 10 seconds).
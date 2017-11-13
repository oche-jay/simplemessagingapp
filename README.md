# simplemessagingapp [![Build Status](https://travis-ci.org/oche-jay/simplemessagingapp.svg?branch=master)](https://travis-ci.org/oche-jay/simplemessagingapp)

This simple messaging app functions as a Web Server that accepts JSON messages sent by HTTP post to a configurable port 
(default port: 8000).

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
A suite of data-driven unit tests can be found in [src/test/java](/src/test/java/) folder.
Some of the tests do print sample message payloads to the console and be these can be used to test
the application manually (e.g. using postman or curl).

 ### Acceptance Criteria                             

  - [x] All sales must be recorded
         
             All valid messages are logged and added to internal, in-memory data-structures
 
  - [x]  All messages must be processed  
             
              All incoming messages are validated, queued and then processed. For invalid messages, 
              a HTTP 400 Status(Bad Request) along with the reason/error message is returned to the user.          

  - [x] After every 10th message received your application should log a report detailing the number
                of sales of each product and their total value. 
                
                Sample report:
                
                ====================================================================
                Sales Report:
                ====================================================================
                Item: gold                 Number sold:   7    Total price:    255403.55
                Item: orange               Number sold:   8    Total price:    88.00
                Item: banana               Number sold:   8    Total price:    88.00
                Item: apple                Number sold:   4    Total price:    32.00
                Item: platinum             Number sold:   16   Total price:    116.81
                Item: pear                 Number sold:   6    Total price:    45.00
                Item: silver               Number sold:   14   Total price:    88422.41
                Item: crude                Number sold:   16   Total price:    2980.11
                --------------------------------------------------------------------
                Grand Total:                                              347175.88
                --------------------------------------------------------------------

                
  - [x] After 50 messages your application should log that it is pausing, stop accepting new messages and log a report of the adjustments that have been made to each sale type while the application was running.      
               
               The server prints the required report and then pauses for a configurable amount of 
               time (default value is 10 seconds).
               
                    ====================================================================
                    Sales Adjustment Report:
                    ====================================================================
                    --------------------------------------------------------------------
                    Item: gold                 Number sold:   7    Total price:    255403.55
                    --------------------------------------------------------------------
                    timestamp: 1510568968598 Value: 3.03 Instruction: ADD 
                    timestamp: 1510568968711 Value: 9.39 Instruction: MULTIPLY 
                    timestamp: 1510568967998 Value: 1.29 Instruction: ADD 
                    timestamp: 1510568968583 Value: 7.24 Instruction: SUBTRACT 
                    timestamp: 1510568968216 Value: 5.77 Instruction: ADD 
                    timestamp: 1510568968187 Value: 0.60 Instruction: ADD 
                    timestamp: 1510568968672 Value: 3.22 Instruction: MULTIPLY 
                    timestamp: 1510568968019 Value: 9.07 Instruction: ADD 
                    timestamp: 1510568968277 Value: 2.92 Instruction: MULTIPLY 
                    timestamp: 1510568968589 Value: 7.90 Instruction: ADD 
                    timestamp: 1510568968484 Value: 8.61 Instruction: MULTIPLY 
                    timestamp: 1510568968495 Value: 7.44 Instruction: MULTIPLY 
                    --------------------------------------------------------------------
                    Item: orange               Number sold:   1    Total price:    5.00
                    --------------------------------------------------------------------
                    --------------------------------------------------------------------
                    Item: platinum             Number sold:   16   Total price:    116.81
                    --------------------------------------------------------------------
                    timestamp: 1510568968029 Value: 3.34 Instruction: ADD 
                    timestamp: 1510568968629 Value: 3.94 Instruction: SUBTRACT 
                    timestamp: 1510568968227 Value: 9.58 Instruction: SUBTRACT 
                    timestamp: 1510568968091 Value: 9.75 Instruction: ADD 
                    timestamp: 1510568968236 Value: 8.41 Instruction: SUBTRACT 
                    timestamp: 1510568968037 Value: 6.69 Instruction: ADD 
                    timestamp: 1510568968153 Value: 3.37 Instruction: ADD 
                    timestamp: 1510568968337 Value: 4.70 Instruction: MULTIPLY 
                    timestamp: 1510568968645 Value: 7.48 Instruction: ADD 
                    --------------------------------------------------------------------
                    Item: pear                 Number sold:   1    Total price:    5.00
                    --------------------------------------------------------------------
                    --------------------------------------------------------------------
                    Item: silver               Number sold:   14   Total price:    88422.41
                    --------------------------------------------------------------------
                    timestamp: 1510568968323 Value: 9.38 Instruction: ADD 
                    timestamp: 1510568968616 Value: 1.36 Instruction: SUBTRACT 
                    timestamp: 1510568968130 Value: 8.04 Instruction: MULTIPLY 
                    timestamp: 1510568968623 Value: 6.88 Instruction: ADD 
                    timestamp: 1510568968695 Value: 8.90 Instruction: SUBTRACT 
                    timestamp: 1510568968522 Value: 9.23 Instruction: SUBTRACT 
                    timestamp: 1510568968177 Value: 2.61 Instruction: SUBTRACT 
                    timestamp: 1510568968207 Value: 3.37 Instruction: ADD 
                    timestamp: 1510568968555 Value: 1.37 Instruction: MULTIPLY 
                    timestamp: 1510568968565 Value: 3.18 Instruction: SUBTRACT 
                    timestamp: 1510568968762 Value: 8.85 Instruction: MULTIPLY 
                    timestamp: 1510568968269 Value: 8.91 Instruction: ADD 
                    timestamp: 1510568968538 Value: 9.41 Instruction: ADD 
                    timestamp: 1510568968820 Value: 4.63 Instruction: ADD 
                    timestamp: 1510568967702 Value: 8.07 Instruction: MULTIPLY 
                    timestamp: 1510568968791 Value: 9.80 Instruction: MULTIPLY 
                    --------------------------------------------------------------------
                    Item: crude                Number sold:   16   Total price:    2980.11
                    --------------------------------------------------------------------
                    timestamp: 1510568968463 Value: 4.41 Instruction: MULTIPLY 
                    timestamp: 1510568968700 Value: 6.35 Instruction: ADD 
                    timestamp: 1510568968256 Value: 5.45 Instruction: SUBTRACT 
                    timestamp: 1510568968010 Value: 9.19 Instruction: MULTIPLY 
                    timestamp: 1510568968569 Value: 7.99 Instruction: SUBTRACT 
                    timestamp: 1510568968508 Value: 8.81 Instruction: SUBTRACT 
                    timestamp: 1510568967756 Value: 6.17 Instruction: SUBTRACT 
                    timestamp: 1510568968476 Value: 7.04 Instruction: MULTIPLY 
                    --------------------------------------------------------------------
                    Grand Total:                                              346932.88
                    --------------------------------------------------------------------


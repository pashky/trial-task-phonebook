#-*- markdown -*-

# Overview

This code consists of a library to access customers XML storage and
demo application to perform CRUD operation on the storage. Demo
application is made of autonomous web server and after start should be
accessed through web browser.

Sample for handled XML file format:

    <Customer xmlns="http://www.arcusys.fi/customer-example">
    <Name>Conan C. Customer</Name>
    <Address>
    <Type>VISITING_ADDRESS</Type>
    <Street>Customer Street 8 B 9</Street>
    <Street>(P.O. Box 190)</Street>
    <PostalCode>12346</PostalCode>
    <Town>Customerville</Town>
    </Address>
    <Phone>
    <Type>WORK_PHONE</Type>
    <Value>+358 555 555 555</Value>
    </Phone>
    <Email>
    <Type>WORK_EMAIL</Type>
    <Value>conan.c.customer@example.com</Value>
    </Email>
    <Phone>
    <Type>MOBILE_PHONE</Type>
    <Value>+358 50 999 999 999</Value>
    </Phone>
    <Notes>                       Conan is a customer.               </Notes>
    </Customer>


# How to build

The app can be built using standard Maven tool run from top level
directory. It well fetch all necessary dependencies during build
process, compile, run some tests and package all-inclusive jar.

    $ mvn package

This command will create self contained .jar file with demo app web
server:

    phonebook-server/target/phonebook-server-1.0-SNAPSHOT-jar-with-dependencies.jar

The pre-built binary is provided in this archive for your convenience.


# How to run

Web server can be run using following command:

    $ java -jar phonebook-server-1.0-SNAPSHOT-jar-with-dependencies.jar sample.xml
    
where `sample.xml` is the name of XML to operate on.
After that please point your browser to 

    http://localhost:8888/ 

and check customer management web application. The web app was tested
in Chrome, Safari and Firefox browsers on Mac but should work in any
modern browser as well.

# Implementation notes and model constraints assumptions

1. The customer storage model generates automatic IDs for customer
records according to natural order in original XML file and further
subsequent additions. This means IDs and thus edit URLs are not
persistent across server startup sessions. Each separate session
should work correctly.

2. Addresses, emails and phones don't have IDs and are kept in unqiue
sets. So, for example, it's impossible to have totally equal emails
with the same type for one customer. Same email with different types
or different emails of the same types is possible though.

3. Phones are considered equal even if they have different amount of
non-significant symbols, so +1 (234) 55-66-77 and +1234556677 are
considered equal. Extra symbols are kept during storage, app doesn't
strip them.

4. There are a few pre-canned types for emails, phones and
addresses. However, if there are other types happen during XML read
file, those are added into respective lists and presented on web UI.

5. XML file is read only once. No further external change tracking. No
external file locking.

6. XML file writes are asynchonous for preformance reasons. So if web
server process is killed unexpectedly, it may loose some very last
changes.

## Simplifications and limitations

1. No paging or limiting support for customers list. It will try to
show everything in database.

2. Validation of user inputs is very rudimentary.



Hi Pavel,

thank you for your application. Our recruitment process starts with an example task, which you will find below.
Can you please return it to me latest Wednesday 20.2. I will get back to you after your task has been checked.
If you have any questions regarding the task, please turn to me.

Kind regards

Minna


rekry@arcusys.fi

Arcusys Oy
Koskikatu 5 C
80100 Joensuu
www.arcusys.fi


___


1. Create corresponding Java object model for XML-fragment below:
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

2. Implement application for handling customer information (basic operations - create, search, update, delete).
Customers information is stored in XML-format to a file (root Customers element contains 0..n Customer objects).
You can use any technology for XML-processing if you like to. Performance characteristics are not important.
You have freedom in choosing implementation technology, but all needed third party libraries should be packaged
together with your solution.
Provide source code and compiled binaries together in one ZIP or TAR archive.

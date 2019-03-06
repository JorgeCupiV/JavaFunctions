package com.jc.functions;

import com.microsoft.azure.functions.annotation.*;
import com.microsoft.azure.functions.*;
import com.google.gson.Gson;
/**
 * Azure Functions with Azure Storage Queue trigger.
 */
public class QueueJsonFunction {
    /**
     * This function will be invoked when a new message is received at the specified path. 
     * The message contents are provided as input to this function.
     */
    @FunctionName("QueueJsonFunction")
    public void run(
        @QueueTrigger(name = "message", queueName = "queuejsonfunctions", connection = "AzureWebJobsStorage") 
        String message,
        final ExecutionContext context) 
    {
        Person person = new Gson().fromJson(message, Person.class);
        context.getLogger().info("New user arrived from Queue. Name: "+
        person.name + " of age:"+ person.age );
    }
}

class Person
{
    String name;
    int age;
}
package com.jc.functions;

import java.io.BufferedWriter;
import java.io.*;

import com.google.gson.Gson;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.annotation.*;
import com.microsoft.azure.storage.*;
import com.microsoft.azure.storage.blob.*;
import com.microsoft.azure.storage.blob.StorageException;

public class QueueJsonToBlobFunction 
{
    @FunctionName("QueueJsonToBlobFunction")
    @StorageAccount("AzureWebJobsStorage")
    public void run(
        @QueueTrigger(name = "message", queueName = "queuejsontoblobfunctions")
        String message,
        final ExecutionContext context) 
    {
        SMS sms = new Gson().fromJson(message, SMS.class);
        String toReturn = "New SMS arrived from "+ sms.name + " :''"+ sms.content+"''";
        String storageConnectionString = System.getenv("AzureWebJobsStorage");

        try
        {
            CloudStorageAccount storageAccount = CloudStorageAccount.parse(storageConnectionString);
            CloudBlobClient blobClient = storageAccount.createCloudBlobClient();
            CloudBlobContainer container = blobClient.getContainerReference("functions-output");

            File sourceFile = File.createTempFile("sms", ".json");
            Writer output = new BufferedWriter(new FileWriter(sourceFile));
            output.write(toReturn);
            output.close();
            CloudBlockBlob blob = container.getBlockBlobReference(sourceFile.getName());
            blob.uploadFromFile(sourceFile.getAbsolutePath());
        }
        catch (StorageException ex)
		{
			System.out.println(String.format("Error returned from the service. Http code: %d and error code: %s", ex.statusCode(), ex.errorCode()));
		}
		catch (Exception ex) 
		{
			System.out.println(ex.getMessage());
        }
        context.getLogger().info(toReturn);
    }
}

class SMS
{
    String content;
    String name;
}
package com.andromeda.cms.service;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.cloudfront.AmazonCloudFrontClient;
import com.amazonaws.services.cloudfront.model.CreateInvalidationRequest;
import com.amazonaws.services.cloudfront.model.CreateInvalidationResult;
import com.amazonaws.services.cloudfront.model.InvalidationBatch;
import com.amazonaws.services.cloudfront.model.Paths;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.Upload;
import com.andromeda.cms.defs.StrapiConstants;

@Service
public class CJDataUploader {
	AWSCredentials credentials = new BasicAWSCredentials(
			  "AKIA4BWNORDW6SLZQ4WH", 
			  "CCok5ZgsvvaoBxxKsAKztA9Z4QOE2/Ak12KHzRIf"
			);
	
	
	public void invalidateFiles (List<String> keys)
	{
		AmazonCloudFrontClient cloudFrontClient = new AmazonCloudFrontClient(credentials);
		
		List<String> newKeys = new ArrayList<>();
		if(keys != null && !keys.isEmpty())
		{
			for (String key : keys) 
			{
				if(!key.startsWith("/"))
					newKeys.add("/" + key);
				else
					newKeys.add(key);
			}
		}
		
		try
		{
			Paths invalidation_paths = new Paths().withItems(newKeys).withQuantity(newKeys.size());
			System.out.println("CJ Invalidation Paths --> " + invalidation_paths );
			String currentTs =  new Timestamp(System.currentTimeMillis()).toString();
			InvalidationBatch invalidation_batch = new InvalidationBatch(invalidation_paths, currentTs );
			CreateInvalidationRequest invalidation = new CreateInvalidationRequest("E1XD904CGM1B4D", invalidation_batch);
			CreateInvalidationResult ret = cloudFrontClient.createInvalidation(invalidation);
		}
		catch (AmazonServiceException ase)
		{
			System.out.println(
					"Caught an AmazonServiceException, which " + "means your request made it "
							+ "to Amazon S3, but was rejected with an error response"
							+ " for some reason.");
			System.out.println("Error Message:    " + ase.getMessage());
			System.out.println("HTTP Status Code: " + ase.getStatusCode());
			System.out.println("AWS Error Code:   " + ase.getErrorCode());
			System.out.println("Error Type:       " + ase.getErrorType());
			System.out.println("Request ID:       " + ase.getRequestId());
		}
		catch (AmazonClientException ace)
		{
			System.out.println("Caught an AmazonClientException, which "
					+ "means the client encountered " + "an internal error while trying to "
					+ "communicate with S3, " + "such as not being able to access the network.");
			System.out.println("Error Message: " + ace.getMessage());
		}
	}
	
	public void uploadFilekeyNameWithRedirection(String keyName, String uploadFileName, String redirectUrl) throws IOException
	{
	
		try
		{
			AmazonS3 s3client = AmazonS3ClientBuilder
					  .standard()
					  .withCredentials(new AWSStaticCredentialsProvider(credentials))
					  .withRegion(Regions.AP_SOUTH_1)
					  .build();
			TransferManager transferManager = new TransferManager(s3client);
			
			File file = new File(uploadFileName);
			PutObjectRequest putObjectRequest = new PutObjectRequest(StrapiConstants.CHITRAJYOTHY_S3_BUCKET_NAME, keyName, file)
		            .withRedirectLocation(redirectUrl);

		    // TransferManager processes all transfers asynchronously,
		    // so this call returns immediately.
		    Upload upload = transferManager.upload(putObjectRequest);
		
		}
		catch (AmazonServiceException ase)
		{
			System.out.println(
					"Caught an AmazonServiceException, which " + "means your request made it "
							+ "to Amazon S3, but was rejected with an error response"
							+ " for some reason.");
			System.out.println("Error Message:    " + ase.getMessage());
			System.out.println("HTTP Status Code: " + ase.getStatusCode());
			System.out.println("AWS Error Code:   " + ase.getErrorCode());
			System.out.println("Error Type:       " + ase.getErrorType());
			System.out.println("Request ID:       " + ase.getRequestId());
		}
		catch (AmazonClientException ace)
		{
			System.out.println("Caught an AmazonClientException, which "
					+ "means the client encountered " + "an internal error while trying to "
					+ "communicate with S3, " + "such as not being able to access the network.");
			System.out.println("Error Message: " + ace.getMessage());
		}
	}
	
	public void uploadFilekeyName(String keyName, String uploadFileName) throws IOException
	{

		try
		{
			AmazonS3 s3client = AmazonS3ClientBuilder
					  .standard()
					  .withCredentials(new AWSStaticCredentialsProvider(credentials))
					  .withRegion(Regions.AP_SOUTH_1)
					  .build();
			
			File file = new File(uploadFileName);
			s3client.putObject(
					StrapiConstants.CHITRAJYOTHY_S3_BUCKET_NAME, 
					keyName, 
					  file
					);
		
		}
		catch (AmazonServiceException ase)
		{
			System.out.println(
					"Caught an AmazonServiceException, which " + "means your request made it "
							+ "to Amazon S3, but was rejected with an error response"
							+ " for some reason.");
			System.out.println("Error Message:    " + ase.getMessage());
			System.out.println("HTTP Status Code: " + ase.getStatusCode());
			System.out.println("AWS Error Code:   " + ase.getErrorCode());
			System.out.println("Error Type:       " + ase.getErrorType());
			System.out.println("Request ID:       " + ase.getRequestId());
		}
		catch (AmazonClientException ace)
		{
			System.out.println("Caught an AmazonClientException, which "
					+ "means the client encountered " + "an internal error while trying to "
					+ "communicate with S3, " + "such as not being able to access the network.");
			System.out.println("Error Message: " + ace.getMessage());
		}
	}
	
	
	public void deleteFilekeyName(String keyName, String deleteFileUrl) throws IOException
	{
		AmazonS3 s3client = AmazonS3ClientBuilder
				  .standard()
				  .withCredentials(new AWSStaticCredentialsProvider(credentials))
				  .withRegion(Regions.AP_SOUTH_1)
				  .build();
		AmazonCloudFrontClient cloudFrontClient = new AmazonCloudFrontClient(credentials);
			
		String deleteFileUrlUpdated;
			try
			{
				boolean startWithSeparator = deleteFileUrl.startsWith("/", 0);
				if(startWithSeparator)
					deleteFileUrlUpdated = (String) deleteFileUrl.subSequence(1, deleteFileUrl.length());
				else
					deleteFileUrlUpdated = deleteFileUrl ;
				
				s3client.deleteObject( StrapiConstants.CHITRAJYOTHY_S3_BUCKET_NAME, deleteFileUrlUpdated);
				System.out.println("file deleted successfully " + deleteFileUrl);
				
				String deleteAmpUrl = deleteFileUrlUpdated + File.separator + "amp";
				s3client.deleteObject( StrapiConstants.CHITRAJYOTHY_S3_BUCKET_NAME, deleteAmpUrl);
				System.out.println("file deleted successfully " + deleteAmpUrl);
				
				//clear cloudfront cache
				Paths invalidation_paths = new Paths().withItems(deleteFileUrl, deleteAmpUrl).withQuantity(2);
				String currentTs =  new Timestamp(System.currentTimeMillis()).toString();
				InvalidationBatch invalidation_batch = new InvalidationBatch(invalidation_paths, currentTs);
				CreateInvalidationRequest invalidation = new CreateInvalidationRequest("E1XD904CGM1B4D", invalidation_batch);
				CreateInvalidationResult ret = cloudFrontClient.createInvalidation(invalidation);
			}
		catch (AmazonServiceException ase)
		{
			System.out.println(
					"Caught an AmazonServiceException, which " + "means your request made it "
							+ "to Amazon S3, but was rejected with an error response"
							+ " for some reason.");
			System.out.println("Error Message:    " + ase.getMessage());
			System.out.println("HTTP Status Code: " + ase.getStatusCode());
			System.out.println("AWS Error Code:   " + ase.getErrorCode());
			System.out.println("Error Type:       " + ase.getErrorType());
			System.out.println("Request ID:       " + ase.getRequestId());
		}
		catch (AmazonClientException ace)
		{
			System.out.println("Caught an AmazonClientException, which "
					+ "means the client encountered " + "an internal error while trying to "
					+ "communicate with S3, " + "such as not being able to access the network.");
			System.out.println("Error Message: " + ace.getMessage());
		}
	}
}

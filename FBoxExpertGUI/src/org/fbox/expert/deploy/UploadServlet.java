package org.fbox.expert.deploy;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;

/**
 * Servlet implementation class NewUploadServlet
 */
@WebServlet("/UploadServlet")
public class UploadServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
	private static final String UPLOADS_DIR = "uploads";
	private static final int MAX_FILE_SIZE = 10 * 1024 * 1024; // 10 MB

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		response.setContentType("text/html;charset=UTF-8");
		
	//	OutputStream out = null;
	//	InputStream filecontent = null;
	//	PrintWriter writer = null;
		
		// Check that we have a file upload request 
		if(ServletFileUpload.isMultipartContent(request)) {
			
			boolean uploadFile = true;
			
			// Create a factory for disk-based file items
			FileItemFactory fItemFactory = new DiskFileItemFactory();
			
			// Create a new file upload handler
			ServletFileUpload fileUploadHandler = new ServletFileUpload(fItemFactory);
			
			try {
				// Parse the request
				List<FileItem> fItems = fileUploadHandler.parseRequest(request);
				
				// Process the uploaded items
				for (FileItem fi : fItems) {
					if (fi.isFormField()) {
						processFormField(fi);
						
					} else {
						String appPath = request.getServletContext().getRealPath("");
						
						sendFileContentAsResponse(fi, response);
						
						if(uploadFile)
							storeFile(fi,appPath);
						
					}
				}
			} catch (FileUploadException e) {
				e.printStackTrace();
			}
		}
	}
		
	private void processFormField(FileItem item) {
		  String name = item.getFieldName();
		  String value = item.getString();
		  
		  System.out.println("[UploadServlet] Item name: " + name +  " | value: " + value);
	}
	
	private void storeFile(FileItem item, String appPath) {
		
		// String fieldName = item.getFieldName();
		String fileName = item.getName();
		
		// String contentType = item.getContentType();
		// boolean isInMemory = item.isInMemory();
		
		long sizeInBytes = item.getSize();
		boolean writeToFile = true;
		
		if(sizeInBytes > MAX_FILE_SIZE) {
			writeToFile = false;
		}
		
		// Process a file upload
		if (writeToFile) {
			try {
				 File uploadedFile = new File(appPath + File.separator + UPLOADS_DIR + File.separator + fileName);
				 
				 System.out.println("[UploadServlet] [INFO] Uploading file to ..." + uploadedFile.toString());
				 
				 if(!uploadedFile.exists()) {
					 uploadedFile.createNewFile();
				 }
				 
				 item.write(uploadedFile);			
				 
				 System.out.println("[UploadServlet] [INFO] File " + fileName + " uploaded successfully!");
				 
				
				 
			} catch (Exception e) {
				System.out.println("[UploadServlet] - [WARN]: Cannot create uploaded file on server..");
				e.printStackTrace();
			}
		}
		else {
			System.out.println("[UploadServlet] - [WARN]: Trying to write a large file.");
		}
		
	}
	
	private void sendFileContentAsResponse(FileItem item, HttpServletResponse response) {
		
		// Send the content of XML as a response...
		InputStream filecontent;
		try {
			filecontent = item.getInputStream();
		
			int read = 0; 
			final byte[] bytes = new byte[1024];
				
			System.out.println("[UploadServlet] [INFO] Reading script...");
			
			while((read = filecontent.read(bytes)) != -1) {
				response.getOutputStream().write(bytes, 0, read);
			}
			
			System.out.println("[UploadServlet] [INFO] Script read successfully!");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
package com.Kimminjae.PerspectiveProjection;

import static org.bytedeco.javacpp.helper.opencv_core.CV_RGB;
import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_imgproc.*;

import org.bytedeco.javacpp.IntPointer;
import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.Pointer;


import static org.bytedeco.javacpp.opencv_imgcodecs.*;


import org.bytedeco.javacpp.opencv_core.CvPoint;
import org.bytedeco.javacpp.opencv_core.CvSeq;
import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.javacv.OpenCVFrameGrabber;
import org.bytedeco.javacv.OpenCVFrameConverter.ToIplImage;



public class DemoCV {
	 static OpenCVFrameConverter.ToIplImage converter = new OpenCVFrameConverter.ToIplImage();
	 
    public static void main(String[] args) throws Exception
    {  
    	
    	CvMemStorage storage = cvCreateMemStorage(10000);
    	CvSeq lines = new CvSeq();
    	@SuppressWarnings("resource")
		CvSeq squares1 = new CvSeq();
    	
    	
        FrameGrabber grabber = FrameGrabber.createDefault(0);
        grabber.start();
        
       
        
       
        IplImage grabbedImage = converter.convert(grabber.grab());
        int width  = grabbedImage.width();
        int height = grabbedImage.height();
        int i =0;
        boolean move = true;
        CanvasFrame frame = new CanvasFrame("My Face", CanvasFrame.getDefaultGamma()/grabber.getGamma());

        while (frame.isVisible() && (grabbedImage = converter.convert(grabber.grab())) != null) {
        	if(i>grabbedImage.width() && move){
        		i=0;
        	}
        	else if(move)
        	{
        		i++;
        	}
        	
        	IplImage gray = cvCreateImage(cvGetSize(grabbedImage), 8, 1);
        	cvCvtColor(grabbedImage, gray, CV_BGR2GRAY);
        	cvCanny(gray, gray, 50, 100, 3);
        	cvCvtColor(gray,grabbedImage,CV_GRAY2BGR);
        	BorderLine liner = new BorderLine(0,0,grabbedImage);
        	liner.moveAndDraw(grabbedImage,grabbedImage,i);
        	FindAndDrawLines(grabbedImage,lines,gray,grabbedImage);
            Frame rotatedFrame = converter.convert(gray);
        
            frame.waitKey(30);
                           
            frame.showImage(rotatedFrame);
            

        }
        frame.dispose();
        
        grabber.stop();
    }
   
    
    public static void captureFrame() {
    	OpenCVFrameGrabber grabber2 = new OpenCVFrameGrabber(0);
    	try
    	{
    		grabber2.start();
    		IplImage img = converter.convert(grabber2.grab());
    		
    		if(img != null)
    		{
    			cvSaveImage("capture.jpg", img);
    		}
    	}
    	catch(Exception ae)
    	{
    		ae.printStackTrace();
    	}
    }
 
    public static void boderDetect(IplImage src , CvSeq lines){
    	//the longest line is the border of projected image
    	
    	
    	
    	
    	
    }
	public static void FindAndDrawLines(IplImage src, CvSeq lines ,IplImage dst, IplImage colorDst)
    {
		
    	CvMemStorage storage = cvCreateMemStorage(0);
    
        if (src == null) {
            System.out.println("Couldn't load source image.");
            return;
        }
//        cvCanny(src, dst, 50, 100, 3);
//        cvCvtColor(dst, colorDst, CV_GRAY2BGR);
        
        lines = cvHoughLines2(dst, storage, CV_HOUGH_PROBABILISTIC, 1, Math.PI / 180, 40, 50, 10, 0, CV_PI);

        for (int i = 0; i < lines.total(); i++) {
            // Based on JavaCPP, the equivalent of the C code:
            // CvPoint* line = (CvPoint*)cvGetSeqElem(lines,i);
            // CvPoint first=line[0], second=line[1]
            // is:
            Pointer line = cvGetSeqElem(lines, i);
            CvPoint pt1  = new CvPoint(line).position(0);
            CvPoint pt2  = new CvPoint(line).position(1);

            cvLine(colorDst, pt1, pt2, CV_RGB(255, 0, 0), 3, CV_AA, 0); // draw the segment on the image
        }
        
//        lines = cvHoughLines2(dst, storage, CV_HOUGH_MULTI_SCALE, 1, Math.PI / 180, 40, 1, 1, 0, CV_PI);
//        for (int i = 0; i < lines.total(); i++) {
//            CvPoint2D32f point = new CvPoint2D32f(cvGetSeqElem(lines, i));
//
//            float rho=point.x();
//            float theta=point.y();
//
//            double a = Math.cos((double) theta), b = Math.sin((double) theta);
//            double x0 = a * rho, y0 = b * rho;
//            CvPoint pt1 = cvPoint((int) Math.round(x0 + 1000 * (-b)), (int) Math.round(y0 + 1000 * (a))), pt2 = cvPoint((int) Math.round(x0 - 1000 * (-b)), (int) Math.round(y0 - 1000 * (a)));
//            
//            cvLine(colorDst, pt1, pt2, CV_RGB(0, 255, 0), 3, CV_AA, 0);
//        }
        
//        lines = cvHoughLines2(dst, storage, CV_HOUGH_STANDARD, 1, Math.PI / 180, 90, 0, 0, 0, CV_PI);
//        for (int i = 0; i < lines.total(); i++) {
//            CvPoint2D32f point = new CvPoint2D32f(cvGetSeqElem(lines, i));
//
//            float rho=point.x();
//            float theta=point.y();
//
//            double a = Math.cos((double) theta), b = Math.sin((double) theta);
//            double x0 = a * rho, y0 = b * rho;
//            CvPoint pt1 = cvPoint((int) Math.round(x0 + 1000 * (-b)), (int) Math.round(y0 + 1000 * (a))), pt2 = cvPoint((int) Math.round(x0 - 1000 * (-b)), (int) Math.round(y0 - 1000 * (a)));
//            System.out.println("Line spotted: ");
//            System.out.println("\t rho= " + rho);
//            System.out.println("\t theta= " + theta);
//            cvLine(colorDst, pt1, pt2, CV_RGB(255, 0, 0), 3, CV_AA, 0);
//        }
    }
	
}
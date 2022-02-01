package ru;

import org.bytedeco.ffmpeg.avcodec.AVPacket;
import org.bytedeco.javacv.*;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicInteger;

//@SpringBootApplication
public class Main {

    public static void main(String[] args) throws Exception {
//       String str = "rtsp://demo:demo@ipvmdemo.dyndns.org:5541/onvif-media/media.amp?profile=profile_1_h264&sessiontimeout=60&streamtype=unicast";
//       String str2 = "rtsp://wowzaec2demo.streamlock.net/vod/mp4:BigBuckBunny_115k.mov";
//        Socket socket = new Socket();
//        socket.connect(new InetSocketAddress("rtsp://wowzaec2demo.streamlock.net", 554));
//        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//        while (true) {
//            System.out.println(reader.readLine());
//        }
//        RTSPControl rtspControl = new RTSPControl("wowzaec2demo.streamlock.net", 554, "/vod/mp4:BigBuckBunny_115k.mov");
//        rtspControl.RTSPOptions();
//        rtspControl.RTSPDescribe();
//        while (rtspControl.RTSPSetup() > 0) ;
//        rtspControl.RTSPPlay();
//        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//        AtomicInteger videoNumber = new AtomicInteger(1);

//        Thread thread = new Thread(() -> {
//            while (true) {
//                try {
////                    outputStream.write(rtspControl.outputStream.toByteArray());
//                    if (rtspControl.outputStream.size() >= Integer.MAX_VALUE-8) {
//                        File file = new File(String.format("video%s.mp4", videoNumber));
//                        file.createNewFile();
//                        FileOutputStream fileOutputStream = new FileOutputStream(file);
//                        fileOutputStream.write(rtspControl.outputStream.toByteArray());
//                        rtspControl.outputStream.reset();
//                        videoNumber.getAndIncrement();
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//        thread.start();
//        Resource resource = new ByteArrayResource(rtspControl.buf);
        FFmpegLogCallback.set();
//        String devicePath = "rtsp://wowzaec2demo.streamlock.net/vod/mp4:BigBuckBunny_115k.mov";
//        String devicePath = "rtsp://localhost:8080/test.sdp";

//        Thread.currentThread().sleep(30_000);
//        rtspControl.sendRTSPRequest(RTSPControl.RTSPRequest.TEARDOWN);

        ForkJoinPool forkJoinPool = new ForkJoinPool();
        List<Callable<Void>> list = new ArrayList<>();
        for (int i = 0; i < 1; i++) {
//            list.add(new MyRunnable(new ByteArrayInputStream(rtspControl.outputStream.toByteArray())));
            list.add(new MyRunnable());
        }

        forkJoinPool.invokeAll(list);
        forkJoinPool.shutdown();
    }

    public static class MyRunnable implements Callable{


//        public MyRunnable(InputStream stream) {
//            this.stream = stream;
//        }

        private InputStream stream;

        @Override
        public java.lang.Void call() {
            String devicePath = "rtsp://127.0.0.1:8080/test.sdp";
            FFmpegFrameGrabber fFmpegFrameGrabber = null;
            try {
                fFmpegFrameGrabber = FFmpegFrameGrabber.createDefault(devicePath);
//                fFmpegFrameGrabber = new FFmpegFrameGrabber(stream);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                fFmpegFrameGrabber.start();
            } catch (FFmpegFrameGrabber.Exception e) {
                e.printStackTrace();
            }
//        Frame frame = fFmpegFrameGrabber.grabImage();
            FFmpegFrameRecorder recorder = null;
            Random random = new Random();
            try {
//                recorder = FFmpegFrameRecorder.createDefault(String.format("Video%s.mov", random.nextInt()), fFmpegFrameGrabber.getImageWidth(), fFmpegFrameGrabber.getImageHeight());
                recorder = FFmpegFrameRecorder.createDefault(String.format("Video%s.mov", random.nextInt()), 720, 580);
                recorder.setFormat("mov");
            } catch (FFmpegFrameRecorder.Exception e) {
                e.printStackTrace();
            }
            recorder.setFrameRate(fFmpegFrameGrabber.getFrameRate());
            recorder.setAudioChannels(fFmpegFrameGrabber.getAudioChannels());
//        FrameRecorder recorder = FrameRecorder.createDefault("Video.mov", fFmpegFrameGrabber.getImageWidth(), fFmpegFrameGrabber.getImageHeight());
            try {
                recorder.start();
            } catch (FFmpegFrameRecorder.Exception e) {
                e.printStackTrace();
            }
            long start = System.currentTimeMillis();
            while (true) {
                Frame frame1 = null;
                try {
                    frame1 = fFmpegFrameGrabber.grab();
                } catch (FFmpegFrameGrabber.Exception e) {
                    e.printStackTrace();
                }
//            Frame frame2 = fFmpegFrameGrabber.grabSamples();
//            AVPacket packet = fFmpegFrameGrabber.grabPacket();
//            byte[] data = frame.data.array();
                try {
                    recorder.record(frame1);
                } catch (FFmpegFrameRecorder.Exception e) {
                    e.printStackTrace();
                }
//            System.out.println("test");
                long current = System.currentTimeMillis();
                if ((current - start) > 10_000){
                    try {
                        recorder.stop();
                    } catch (FFmpegFrameRecorder.Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        fFmpegFrameGrabber.release();
                        fFmpegFrameGrabber.stop();
                    } catch (FFmpegFrameGrabber.Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
            return null;
        }
    }
}

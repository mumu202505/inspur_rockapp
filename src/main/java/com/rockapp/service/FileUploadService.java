package com.rockapp.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

public interface FileUploadService {
    /**
     * 上传文件快
     * @param chunk
     * @param chunkNumber
     * @param md5
     */
    public String uploadChunk(MultipartFile chunk, int chunkNumber, String md5,String fileNameint,int totalchunks) throws Exception;

    /**
     * 获取已上传的文件块
     * @param md5
     * @return
     */
    public Set<Integer> getUploadedChunks(String md5);

    /**
     * 合并文件块
     * @param md5
     * @param fileName
     * @return
     */
    public String mergeChunks(String md5, String fileName)throws Exception;

}

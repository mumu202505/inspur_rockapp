package com.rockapp.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lowagie.text.Element;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.ColumnText;
import com.lowagie.text.pdf.PdfWriter;
import com.rockapp.dto.BaseVersionHistoryUplodDto;
import com.rockapp.entity.BaseVersionHistoryEntity;
import com.rockapp.mapper.BaseVersionHistoryMapper;
import com.rockapp.service.BaseVersionHistoryService;
import com.rockapp.utils.CommonBeanUtils;
import com.rockapp.utils.MinIOUtils;
import fr.opensagres.poi.xwpf.converter.pdf.PdfConverter;
import fr.opensagres.poi.xwpf.converter.pdf.PdfOptions;
import fr.opensagres.xdocreport.itext.extension.font.IFontProvider;
import io.minio.errors.*;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.lowagie.text.Font;


@Service("baseVersionHistoryService")
@Transactional
public class BaseVersionHistoryServiceImpl extends ServiceImpl<BaseVersionHistoryMapper, BaseVersionHistoryEntity> implements BaseVersionHistoryService {

    @Autowired
    BaseVersionHistoryMapper baseVersionHistoryMapper;

    @Autowired
    private MinIOUtils minioUtil;

    @Value("${minio.bucketName}")
    private String bucketName;

    @Value("${minio.url}")
    private String minIoUrl;

    @Override
    public BaseVersionHistoryEntity getAppVersion(String type) {
        BaseVersionHistoryEntity baseVersionHistoryEntity = baseVersionHistoryMapper.selectOne(Wrappers.<BaseVersionHistoryEntity>lambdaQuery()
                .eq(BaseVersionHistoryEntity::getFVersionType, type)
                .orderByDesc(BaseVersionHistoryEntity::getFVersionCode) // 按字段降序
                .last("LIMIT 1"));
        return baseVersionHistoryEntity;
    }

    @Override
    public List<BaseVersionHistoryEntity> getAllAiVersion() {
        List<BaseVersionHistoryEntity> baseVersionHistoryEntities = baseVersionHistoryMapper.selectList(Wrappers.<BaseVersionHistoryEntity>lambdaQuery()
                .eq(BaseVersionHistoryEntity::getFVersionType, 3));
        return baseVersionHistoryEntities;
    }

    @Override
    @Async
    public void uploadApp(MultipartFile file, BaseVersionHistoryUplodDto versionHistoryUplodDto) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
//        try {
        String objectName = minioUtil.upload(file);
        if (null != objectName) {
            String url = minIoUrl + "/" + bucketName + "/" + objectName;
            BaseVersionHistoryEntity baseVersionHistoryEntity = CommonBeanUtils.dtoTransfer(versionHistoryUplodDto, BaseVersionHistoryEntity.class);
            baseVersionHistoryEntity.setFVersionUrl(url);
            baseVersionHistoryMapper.insert(baseVersionHistoryEntity);
        } else {
            throw new SecurityException("上传失败");
        }
    }


    //    public void convertWordToPdf(MultipartFile file, HttpServletResponse response) {
//        try {
//            // Set response content type
//            response.setContentType("application/pdf");
//
//            // Set the Content-Disposition header to suggest a filename for download
//            String originalFilename = file.getOriginalFilename();
//            String pdfFilename = originalFilename.replace(".docx", ".pdf");
//            // 对文件名进行URL编码（关键步骤）
//            String encodedFilename = URLEncoder.encode(pdfFilename, StandardCharsets.UTF_8.name())
//                    .replace("+", "%20"); // 替换空格编码
//
//            response.setHeader("Content-Disposition",
//                    "attachment; filename*=UTF-8''" + encodedFilename);
//            // Get input stream from MultipartFile
//            InputStream docxInputStream = file.getInputStream();
//
//            // Get output stream from HttpServletResponse
//            OutputStream outputStream = response.getOutputStream();
//
//            // Convert DOCX to PDF
//            IConverter converter = LocalConverter.builder().build();
//            converter.convert(docxInputStream)
//                    .as(DocumentType.DOCX)
//                    .to(outputStream)
//                    .as(DocumentType.PDF)
//                    .execute();
//
//            // Close streams
//            outputStream.flush();
//            docxInputStream.close();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            // You might want to set an appropriate HTTP error status here
//            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
//            try {
//                response.getWriter().write("Error converting file: " + e.getMessage());
//            } catch (IOException ex) {
//                ex.printStackTrace();
//            }
//        }
//    }
    @Override
    public void convertWordToPdf(MultipartFile file, HttpServletResponse response) throws IOException {
        // 1. 验证文件类型
//        if (!file.getContentType().equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document")) {
//            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "只支持.docx文件");
//            return;
//        }

        // 2. 设置响应头
        response.setContentType("application/pdf");
        String filename = file.getOriginalFilename().replace(".docx", ".pdf");
        String encodedFilename = URLEncoder.encode(filename, StandardCharsets.UTF_8.toString())
                .replaceAll("\\+", "%20");

        response.setHeader("Content-Disposition",
                "attachment; filename=\"" + encodedFilename + "\"; filename*=UTF-8''" + encodedFilename);

        try (InputStream in = file.getInputStream();
             OutputStream out = response.getOutputStream()) {

            // 3. 加载Word文档
            XWPFDocument document = new XWPFDocument(in);

            // 4. 配置PDF选项（特别处理标题和页码）
            PdfOptions options = PdfOptions.create()
                    .fontProvider(new IFontProvider() {
                        // 预定义中文字体映射
                        private final Map<String, String> chineseFontMap = Map.of(
                                "宋体", "SimSun",
                                "黑体", "SimHei",
                                "微软雅黑", "Microsoft YaHei",
                                "楷体", "KaiTi",
                                "仿宋", "FangSong"
                        );

                        @Override
                        public com.lowagie.text.Font getFont(String familyName, String encoding,
                                                             float size, int style, Color color) {
                            try {
                                // 特别处理标题字体 - 确保标题编号格式正确
                                if (familyName != null && familyName.toLowerCase().contains("heading")) {
                                    familyName = "黑体"; // 标题统一使用黑体
                                }

                                String mappedName = chineseFontMap.getOrDefault(familyName, familyName);

                                // 从resources/fonts目录加载字体文件
                                String fontPath = "/fonts/" + mappedName + ".ttf";
                                InputStream fontStream = getClass().getResourceAsStream(fontPath);

                                if (fontStream != null) {
                                    BaseFont baseFont = BaseFont.createFont(
                                            fontPath,
                                            BaseFont.IDENTITY_H,
                                            BaseFont.EMBEDDED,
                                            true,
                                            fontStream.readAllBytes(),
                                            null
                                    );
                                    return new com.lowagie.text.Font(baseFont, size, style, color);
                                }
                            } catch (Exception e) {
                                System.err.println("字体加载失败: " + familyName + ", 错误: " + e.getMessage());
                            }

                            // 备用方案
                            try {
                                BaseFont baseFont = BaseFont.createFont("STSong-Light",
                                        BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
                                return new com.lowagie.text.Font(baseFont, size, style, color);
                            } catch (Exception e) {
                                return new com.lowagie.text.Font(
                                        com.lowagie.text.Font.HELVETICA,
                                        size,
                                        style,
                                        color);
                            }
                        }
                    });
            // 5. 转换为PDF
            PdfConverter.getInstance().convert(document, out, options);

            out.flush();
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "转换失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
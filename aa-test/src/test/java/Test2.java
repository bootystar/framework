import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import io.github.bootystar.mybatisplus.generator.ParentGenerator;
import io.github.bootystar.mybatisplus.generator.core.CustomMapper;
import io.github.bootystar.mybatisplus.generator.core.CustomService;
import io.github.bootystar.mybatisplus.generator.core.CustomServiceImpl;

import java.util.Collections;


/**
 * @author booty
 * @since 2023/7/13 14:44
 */
public class Test2 {

    private static String url ="jdbc:mysql://localhost:3306/test?useUnicode=true&characterEncoding=UTF-8";
    private static String username ="root";
    private static String password ="root";
    public static void main(String[] args) {
        String projectPath = System.getProperty("user.dir");
        ParentGenerator generator = new ParentGenerator(url, username, password);

        generator
                .globalConfigBuilder()
                .dateType(DateType.ONLY_DATE)
//                .enableSwagger()
                .outputDir(projectPath+ "/aa-test/src/main/java")
        ;
        generator
                .packageConfigBuilder()
                .parent("bootystar.test")
                .entity("entity.pojo")
//                .pathInfo(Collections.singletonMap(OutputFile.xml, projectPath + "/aa-test/src/main/resources/xml"))

        ;  // 指定输出目录
        generator
                .customConfigBuilder()
                .jakartaApi(false)
                .enableValidated(true)
                .orderColumn("age",true)
                .orderColumn("name", false)
                .orderColumn("id_card", true)
                .DTOPackage("entity.dto")
                .VOPackage("entity.vo")
                .exportOnVO(true)
                .importOnVO(true)
//                .VOFieldAnnotation(true)
//                .VOResultMap(false)
//                .restStyle(true)
//                .allPost(true)

//                .requestBody(true)
//                .enableOrigins(true)
//                .generateDelete(true)
//                .voResultMap(true)
//                .generateUpdate(false)
                .generateExport(true)
                .generateImport(true)
//                .generateInsert(false)
//                .generateDelete(false)
//                .generateSelect(true)
                .showServiceImplMethod(true)
                .showMapperMethod(true)


        ;
        generator.strategyConfigBuilder()
                .entityBuilder()
                .enableFileOverride()
                .enableActiveRecord()
                .idType(IdType.ASSIGN_ID)
                .enableTableFieldAnnotation()
                .logicDeleteColumnName("deleted")
        ;

        generator.strategyConfigBuilder()
                .serviceBuilder()
                .enableFileOverride()
                .superServiceClass(CustomService.class)
                .superServiceImplClass(CustomServiceImpl.class)
            ;

        generator.strategyConfigBuilder()
                .mapperBuilder().superClass(CustomMapper.class).enableFileOverride()
        ;
        generator.strategyConfigBuilder()
                .entityBuilder()
                .enableLombok()
                .enableFileOverride()
        ;

        ;
        generator.globalConfigBuilder()
//                .enableSpringdoc()
//                .enableSwagger()
                ;

        generator.execute("user");
    }

}
// ... 其他配置保持不变 ...

.strategyConfig(builder -> {
    builder.addInclude("university_major")
        .addTablePrefix("t_", "c_");
    builder.controllerBuilder()
        .enableRestStyle();
    builder.entityBuilder()
        .enableLombok()
        .enableTableFieldAnnotation()
        // 添加以下两行注解配置
        .annotation("@Data")
        .annotation("@Accessors(chain = true)");
})

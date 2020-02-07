# shiro 权限控制 

### shiro 配置
#### 1.将需要进行权限控制才能访问的uri，通过数据库读取进行设置
#### 2.无需再通过 @RequiresPermissions("role:list")
      // 加载数据库中配置的资源权限列表
        List<SysMenu> sysMenus = sysMenuService.listUrlAndPermission();
        for (SysMenu sysMenu : sysMenus) {
            if (!StringUtils.isEmpty(sysMenu.getUrl()) && !StringUtils.isEmpty(sysMenu.getPerms())) {
                String permission = "perms[" + sysMenu.getPerms() + "]";
                filterChainDefinitionMap.put(sysMenu.getUrl(), permission);
            }
        }
        // 本例子中并不存在什么特别关键的操作，所以直接使用user认证。如果有朋友是参考本例子的shiro开发其他安全功能（比如支付等）时，建议针对这类操作使用authc权限 by yadong.zhang
        filterChainDefinitionMap.put("/**", "user,kickout");
#### application.properties
     redis缓存服务配置
     spring.session.store-type=redis


### 限制同时登录的个数
##### 类：ShiroConfig
##### 类：KickoutSessionControlFilter
       //自定义过滤器
        Map<String, Filter> filtersMap = new LinkedHashMap<String, Filter>();
        //限制同一帐号同时在线的个数。
        filtersMap.put("kickout", kickoutSessionControlFilter());
        shiroFilterFactoryBean.setFilters(filtersMap);


### 跨域配置
#### 类 CorsRequestFilter
#### 通过再controller配置如下注释
@CrossOrigin(origins = "http://localhost:8888", maxAge = 3600)

### swagger2配置
#### 类 Swagger2Config
注释默认开启swagger
@ConditionalOnProperty(prefix = "mconfig", name = "swagger-ui-open", havingValue = "true")

#### application.properties 配置是否开启swagger
mconfig.swagger-ui-open=true
#### 类：WebMvcConfig
        /**
         * swagger 静态资源映射
         */
        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
#### 类 ShiroServiceImpl
        //swagger配置，无需登录即可访问,否则必须登录才能访问
        filterChainDefinitionMap.put("/swagger-ui.html", "anon");
        filterChainDefinitionMap.put("/webjars/**", "anon");
        filterChainDefinitionMap.put("/v2/**", "anon");
        filterChainDefinitionMap.put("/swagger-resources/**", "anon");

### 拦截器uri配置
#### 类：WebMvcConfig配置需要放行和拦截的uri
#### 类：RememberAuthenticationInterceptor
 @Autowired
    private RememberAuthenticationInterceptor rememberAuthenticationInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(rememberAuthenticationInterceptor)
                .excludePathPatterns("/passport/**", "/error/**", "/static/assets/**", "favicon.ico")
                .addPathPatterns("/**");
    }
    
    
### 认证和授权
#### 类 ShiroRealm
#### 登录后，第一次访问需要权限控制的uri时，根据登录用户查询用户角色和权限
        // 赋予角色
        List<SysRole> roleList = roleService.listRolesByUserId(userId);
        for (SysRole SysRole : roleList) {
            info.addRole(SysRole.getRoleName());
        }

        // 赋予权限
        List<SysMenu> sysMenus = sysMenuService.listByUserId(userId);
        if (!CollectionUtils.isEmpty(sysMenus)) {
            for (SysMenu sysMenu : sysMenus) {
                String permission = sysMenu.getPerms();
                logger.info(sysMenu.getMenuName() + "   " + permission);
                if (!StringUtils.isEmpty(permission)) {
                    info.addStringPermission(permission);
                }
            }
        }
        
        
### 参考 https://gitee.com/ssh123/shiro 码运的shiro使用             
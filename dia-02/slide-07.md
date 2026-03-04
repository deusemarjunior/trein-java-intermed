# Slide 7: Spring Boot DevTools

**Horário:** 15:15 - 15:30

---

## Hot Reload Automático! 🔥

```xml
<!-- pom.xml -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-devtools</artifactId>
    <scope>runtime</scope>
    <optional>true</optional>
</dependency>
```

---

## O que ele faz?

✅ Restart automático ao salvar arquivo  
✅ LiveReload no browser  
✅ Configurações otimizadas para dev  
✅ Cache desabilitado  

---

## 🎬 DEMO

1. Adicionar DevTools
2. Mudar um controller
3. Salvar (Ctrl+S)
4. Aplicação reinicia automaticamente (~2s)

---

## Configurar no IntelliJ

```
Settings → Build, Execution, Deployment → Compiler
✅ Build project automatically

Settings → Advanced Settings
✅ Allow auto-make to start even if developed application is currently running
```

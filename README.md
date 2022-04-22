# İzibiz Soap Java Örnek Entegrason İstemcisi

İzibiz web servisleri için entegrasyon örneği olarak hazırlanmıştır. Kimlik Doğrulama, E-Fatura, E-Arşiv, E-İrsaliye, E-Müstahsil, E-Serbest Meslek ve E-Mutabakat ürünlerini kapsamaktadır. Her bir ürün için bir adet adaptörümüz bulunmakta ve bu adaptör sınıflarımız birer Spring component'i olarak yazılmıştır. Böylece, adaptör sınıflarımız ister autowiring kullanılarak, ister Client (Main class) instance'si oluşturularak bu instance üzerinden çağrılarak kullanılabilir. Test metodlarımız genellikle birbiriyle bağlantılı olduğu için metodlarımızı 'run' yerine 'debug' modunda çalıştırıp satır satır ilerletmeniz tavsiye edilir.



## Kurulum

### Intellij
Kurulum gerekmemektedir. Kullanım kısmında belirtilen adımları uygulayınız.

### Eclipse
pom.xml dosyasını [bu dosya](https://github.com/izibiz/izibiz-java-soap-client/files/8542638/pom.zip) ile değiştirdikten sonra aşağıdaki adımlar tekrarlanmalıdır.
  1. pom.xml -> sağ tık -> run as -> maven clean
  2. pom.xml -> sağ tık -> run as -> maven generate-sources
  3. pom.xml -> sağ tık -> run as -> maven update project



## Kullanım

  1. Repository URL'i kullanılarak veya kaynak kodlar indirilerek proje herhangi bir IDE içerisinde açılır
  2. (Eclipse kullanılıyor ise) kurulum kısmında belirtilen adımlar uygulanır
  3. (Intellij kullanılıyor ise) WSDL classlarının oluşturulması için pom.xml dosyasında 'maven compile' komutu çalıştırılır
  4. Target package'ine (Eclipse için farklı olabilir) gidilerek auto-generated classların oluşturulup oluşturulmadığı kontrol edilir
  5. (Intellij kullanılıyor ise) Projeye rebuild yapılır 
  6. AuthTests sınıfında bulunan USERNAME ve PASSWORD alanlarına gerekli bilgiler yazılır
  7. Test classlarına sağ tıklanarak run veya debug yapılır (debug önerilir)
  8. Sonuçlar return tipi olarak döndürülen Response objelerinin içleri incelenerek kontrol edilir


# İzibiz Soap Java Örnek Entegrason İstemcisi

İzibiz web servisleri için entegrasyon örneği olarak hazırlanmıştır. Kimlik Doğrulama, E-Fatura, E-Arşiv, E-İrsaliye, E-Müstahsil, E-Serbest Meslek ve E-Mutabakat ürünlerini kapsamaktadır. Her bir ürün için bir adet adaptörümüz bulunmakta ve bu adaptör sınıflarımız birer Spring component'i olarak yazılmıştır. Böylece, adaptör sınıflarımız ister autowiring kullanılarak, ister Client (Main class) instance'si oluşturularak bu instance üzerinden çağrılarak kullanılabilir. Test metodlarımız genellikle birbiriyle bağlantılı olduğu için metodlarımızı 'run' yerine 'debug' modunda çalıştırıp satır satır ilerletmeniz tavsiye edilir.



## Kurulum
Repository URL'i kullanılarak veya kaynak kodlar indirilerek proje herhangi bir IDE içerisinde açılır

### Intellij
  1. wsimport komutlarının çalışması için için pom.xml dosyasında 'maven compile' komutu çalıştırılır
  2. Projeye rebuild yapılır

### Eclipse
pom.xml dosyasını [bu dosya](https://github.com/izibiz/izibiz-java-soap-client/files/8542638/pom.zip) ile değiştirdikten sonra aşağıdaki adımlar tekrarlanmalıdır.
  1. pom.xml -> sağ tık -> run as -> maven clean
  2. pom.xml -> sağ tık -> run as -> maven generate-sources
  3. pom.xml -> sağ tık -> run as -> maven update project

Kurulum tamamlandıktan sonra 'target' package'ine (Eclipse için farklı olabilir) gidilerek auto-generated classların oluşturulup oluşturulmadığı kontrol eidlmelidir. Bu noktada artık projemizde hata bulunmamalı ve başarıyla build yapılabiliyor olmalıdır.


## Kullanım
  
  1. AuthTests sınıfında bulunan USERNAME ve PASSWORD alanlarına gerekli bilgiler yazılır
  2. Test classlarına sağ tıklanarak run veya debug yapılır (debug önerilir)
  3. Sonuçlar return tipi olarak döndürülen Response objelerinin içleri incelenerek kontrol edilir


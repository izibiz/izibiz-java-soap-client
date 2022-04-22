# İzibiz Soap Java Örnek Entegrason İstemcisi

İzibiz web servisleri için entegrasyon örneği olarak hazırlanmıştır. Kimlik Doğrulama, E-Fatura, E-Arşiv, E-İrsaliye, E-Müstahsil, E-Serbest Meslek ve E-Mutabakat ürünlerini kapsamaktadır. Her bir ürün için bir adet adaptörümüz bulunmakta ve bu adaptör sınıflarımız birer Spring component'i olarak yazılmıştır. Böylece, adaptör sınıflarımız ister autowiring kullanılarak, ister Client (Main class) instance'si oluşturularak bu instance üzerinden çağrılarak kullanılabilir. Test metodlarımız genellikle birbiriyle bağlantılı olduğu için metodlarımızı 'run' yerine 'debug' modunda çalıştırıp satır satır ilerletmeniz tavsiye edilir.



## Kurulum

### Intellij
Değişiklik yapmaya gerek yoktur

### Eclipse
pom.xml dosyasını [bu dosya](https://github.com/izibiz/izibiz-java-soap-client/files/8542638/pom.zip) ile değiştirdikten sonra kullanıma hazır olmalıdır.



## Kullanım

  1. Repository URL'i kullanılarak veya kaynak kodlar indirilerek proje herhangi bir IDE içerisinde açılır (Tercihen Intellij IDEA)
  2. WSDL classlarının oluşturulması için pom.xml dosyasında 'maven compile' komutu çalıştırılır
  3. Target package'ine (Eclipse için farklı olabilir) gidilerek auto-generated classların oluşturulup oluşturulmadığı kontrol edilir
  4. Projeye rebuild yapılır 
  5. AuthTests sınıfında bulunan USERNAME ve PASSWORD alanlarına gerekli bilgiler yazılır
  6. Test classlarına sağ tıklanarak run veya debug yapılır (debug önerilir)
  7. Sonuçlar return tipi olarak döndürülen Response objelerinin içleri incelenerek kontrol edilir


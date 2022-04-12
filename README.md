# İzibiz Soap Java Örnek Entegrason İstemcisi

İzibiz web servisleri için entegrasyon örneği olarak hazırlanmıştır. Kimlik Doğrulama, E-Fatura, E-Arşiv, E-İrsaliye, E-Müstahsil, E-Serbest Meslek ve E-Mutabakat ürünlerini kapsamaktadır. Her bir ürün için bir adet adaptörümüz bulunmakta ve bu adaptör sınıflarımız birer Spring component'i olarak yazılmıştır. Böylece, ister autowiring kullanılarak, ister Client (Main class) instance'si oluşturularak bu instance üzerinden çağrılarak kullanılabilir.

# Kullanım:
  1. Repository URL'i kullanılarak kaynak kodlar herhangi bir IDE içerisinde açılır
  2. AuthTests sınıfında bulunan USERNAME ve PASSWORD alanlarına gerekli bilgiler yazılır
  3. Test classlarına sağ tıklanarak run veya debug yapılır (debug önerilir)
  4. Sonuçlar return tipi olarak döndürülen Response objelerinin içleri incelenerek kontrol edilir

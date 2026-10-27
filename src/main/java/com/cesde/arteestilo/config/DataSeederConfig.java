package com.cesde.arteestilo.config;

import com.cesde.arteestilo.model.Categoria;
import com.cesde.arteestilo.model.PerfilUsuario;
import com.cesde.arteestilo.model.Producto;
import com.cesde.arteestilo.model.Usuario;
import com.cesde.arteestilo.repository.CategoriaRepository;
import com.cesde.arteestilo.repository.ProductoRepository;
import com.cesde.arteestilo.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.List;

@Configuration
public class DataSeederConfig {

    @Bean
    public CommandLineRunner initData(
            UsuarioRepository usuarioRepository,
            CategoriaRepository categoriaRepository,
            ProductoRepository productoRepository) {

        return args -> {
            // Si ya hay usuarios, significa que la base de datos ya tiene datos.
            // Evitamos duplicar la información en cada reinicio.
            if (usuarioRepository.count() > 0) {
                System.out.println("✅ Los datos iniciales ya existen. Omitiendo DataSeeder.");
                return;
            }

            System.out.println("⏳ Sembrando datos iniciales en la base de datos...");

            // 1. Crear un Usuario con su Perfil
            PerfilUsuario perfil = new PerfilUsuario();
            perfil.setBiografia("Cliente frecuente de muebles de lujo");
            perfil.setFotoPerfilUrl("https://ejemplo.com/santiago.jpg");

            Usuario usuario = new Usuario();
            usuario.setNombre("Santiago Espinosa");
            usuario.setCorreo("santiago@cesde.net");
            usuario.setTelefono("3001234567");
            usuario.setPerfilUsuario(perfil);
            perfil.setUsuario(usuario); // Relación bidireccional
            usuarioRepository.save(usuario);

            // 2. Crear Categorías
            Categoria sillas = Categoria.builder().nombre("Sillas").descripcion("Sillas de comedor y oficina").build();
            Categoria mesas = Categoria.builder().nombre("Mesas").descripcion("Mesas de centro y comedor").build();
            categoriaRepository.saveAll(List.of(sillas, mesas));

            // 3. Crear Productos
            Producto sillaNordica = Producto.builder()
                    .nombre("Silla Nórdica de Roble")
                    .precio(new BigDecimal("350000.00"))
                    .stock(10)
                    .imagenUrl("https://ejemplo.com/silla.jpg")
                    .categoria(sillas)
                    .build();

            Producto mesaVidrio = Producto.builder()
                    .nombre("Mesa de Centro en Vidrio")
                    .precio(new BigDecimal("1200000.00"))
                    .stock(5)
                    .imagenUrl("https://ejemplo.com/mesa.jpg")
                    .categoria(mesas)
                    .build();

            Producto sofaLujo = Producto.builder()
                    .nombre("Sofá de Lujo Italiano")
                    .precio(new BigDecimal("2500000.00"))
                    .stock(2)
                    .imagenUrl("https://ejemplo.com/sofa.jpg")
                    .categoria(mesas) // Lo puse en mesas para no crear otra categoría, puedes cambiarlo
                    .build();

            productoRepository.saveAll(List.of(sillaNordica, mesaVidrio, sofaLujo));

            System.out.println("✅ Datos iniciales sembrados exitosamente.");
        };
    }
}
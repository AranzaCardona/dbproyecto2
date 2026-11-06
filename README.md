# Inventario Tienda Minisuper Utemita

Aplicación de consola en **Java 17 + Maven + PostgreSQL (JDBC)** para gestionar productos,
categorías, clientes, ventas, proveedores y compras.

## Requisitos
- Java 17 (JDK)
- Maven
- Docker y Docker Compose
- Git

## Puesta en marcha (primera vez)

```bash
# 1. Clonar el repositorio
git clone git@github.com:ewalexj/dbproyecto2.git
cd dbproyecto2

# 2. Crear tu archivo de variables (y editarlo con tus contraseñas)
cp .env.example .env

# 3. Levantar PostgreSQL y pgAdmin
docker compose up -d

# 4. Crear las tablas y cargar datos de prueba
docker exec -i proyecto2 psql -U admin -d admin < tablas.sql
docker exec -i proyecto2 psql -U admin -d admin < db/datos_prueba.sql
```

- PostgreSQL queda en `localhost:5433` (usuario `admin`, base `admin`).
- pgAdmin queda en http://localhost:8081 (usuario `admin@admin.com`).

Cada persona tiene **su propia base de datos local**. Lo que se comparte por GitHub es el
esquema (`tablas.sql`), no los datos.

## Ejecutar la aplicación

```bash
# Cargar las variables de .env en la terminal actual (no imprime nada)
set -a && source .env && set +a

# Compilar y ejecutar
mvn compile exec:java -Dexec.mainClass=com.proyecto.Main
```

## Estructura del código

```
src/main/java/com/proyecto/
├── Main.java          Arranca: conexión + menús
├── BaseDatos.java     Conexión con DB_URL, DB_USER, DB_PASSWORD
├── Consola.java       Único Scanner y lectura validada del teclado
├── ErroresSql.java    Códigos SQLSTATE (23503, 23505, 23514)
├── menu/              Menús: piden datos e imprimen resultados (sin SQL)
├── dao/               Acceso a datos: solo SQL con PreparedStatement
└── modelo/            Records con los datos de cada tabla
```

Las consultas pendientes están marcadas con `// TODO SQL` en `dao/`. Cada una indica sus
parámetros y las columnas que Java espera leer.

## Cómo colaborar

Usamos **GitHub Flow**: `main` siempre compila y todo cambio entra por Pull Request.

```bash
# 1. Partir siempre de main actualizado
git switch main
git pull

# 2. Crear una rama para tu tarea
git switch -c feature/sql-clientes

# 3. Trabajar, compilar y hacer commits pequeños
mvn compile
git status                        # revisa que NO aparezca .env
git add src/main/java/com/proyecto/dao/ClienteDao.java
git commit -m "SQL de ClienteDao"

# 4. Subir la rama
git push -u origin feature/sql-clientes
```

5. En GitHub, abre un **Pull Request** hacia `main` y llena la plantilla.
6. La otra persona lo revisa y aprueba; GitHub Actions comprueba que compile.
7. Se hace **Merge** y ambos actualizan: `git switch main && git pull`.

Si `main` cambió mientras trabajabas, desde tu rama: `git pull origin main`, resuelve
conflictos si los hay, compila y vuelve a hacer `git push`.

### Reglas
- Una rama y un PR por tarea; nombres como `feature/...` o `fix/...`.
- Compilar antes de hacer push.
- Nunca subir `.env` ni contraseñas.
- Las tareas se registran como **Issues**; escribe `Closes #N` en el PR para cerrarlas.

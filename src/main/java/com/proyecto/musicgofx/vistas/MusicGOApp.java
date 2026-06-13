package vistas;

import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

import com.proyecto.musicgofx.modelo.persistencia.RepositorioDatos;
import com.proyecto.musicgofx.modelo.entidades.*;
import com.proyecto.musicgofx.modelo.servicios.*;
import com.proyecto.musicgofx.excepciones.*;
import com.proyecto.musicgofx.util.Validadores;

/**
 * ============================================================
 *  MUSICGO - INTERFAZ GRAFICA "GALERIA NOIR" (CORREGIDA)
 * ============================================================
 *  Correcciones:
 *  1. Botones del sidebar funcionales con ActionListener
 *  2. "Salir" vuelve al login, no cierra la app
 *  3. Una sola ventana de playlists (panel lateral eliminado)
 *  4. Playlists muestran contenido al hacer click
 *  5. Explorar con filtros funcionales (Todo, Canciones, Podcasts, Productos)
 */
public class MusicGOApp extends JFrame {

    // ============================================================
    //  PALETA DE COLORES - GALERIA NOIR
    // ============================================================
    public static final Color NOIR_BLACK      = new Color(11, 11, 11);
    public static final Color NOIR_DARK       = new Color(43, 43, 43);
    public static final Color NOIR_GRAY       = new Color(80, 80, 80);
    public static final Color NOIR_BROWN      = new Color(166, 138, 109);
    public static final Color NOIR_BEIGE_MED  = new Color(216, 197, 173);
    public static final Color NOIR_BEIGE      = new Color(243, 231, 214);
    public static final Color NOIR_CREAM      = new Color(250, 245, 235);
    public static final Color NOIR_RED        = new Color(180, 80, 60);

    // ============================================================
    //  FUENTES
    // ============================================================
    public static final Font FONT_DISPLAY  = new Font("Georgia", Font.BOLD, 42);
    public static final Font FONT_TITLE    = new Font("Georgia", Font.BOLD, 28);
    public static final Font FONT_SUBTITLE = new Font("Georgia", Font.ITALIC, 18);
    public static final Font FONT_BODY     = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_SMALL    = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_BUTTON   = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_MONO     = new Font("Consolas", Font.PLAIN, 12);

    // ============================================================
    //  SERVICIOS
    // ============================================================
    private GestorUsuarios gestorUsuarios;
    private GestorCatalogo gestorCatalogo;
    private GestorPlaylists gestorPlaylists;
    private GestorReproduccion gestorReproduccion;
    private GestorCompras gestorCompras;
    private GestorEstadisticas gestorEstadisticas;

    // ============================================================
    //  ESTADO
    // ============================================================
    private Usuario usuarioActual = null;
    private Audio audioReproduciendo = null;
    private boolean sidebarVisible = true;
    private Timer viniloTimer;
    private double anguloVinilo = 0;
    private boolean reproduciendo = false;
    private String vistaActual = "inicio";

    // Componentes UI
    private JPanel panelCentral;
    private JPanel panelSidebar;
    private JPanel panelReproductor;
    private JPanel panelVinilo;
    private JLabel lblInfoCancion;
    private JLabel lblInfoArtista;
    private JProgressBar progressBar;
    private JButton btnPlay;
    private Timer timerReproduccion;
    private int segundosReproduccion = 0;
    private JLabel lblUsuarioSidebar;

    // ============================================================
    //  CONSTRUCTOR
    // ============================================================
    public MusicGOApp(GestorUsuarios gu, GestorCatalogo gc, GestorPlaylists gp,
                      GestorReproduccion gr, GestorCompras gcom, GestorEstadisticas ge) {
        this.gestorUsuarios = gu;
        this.gestorCatalogo = gc;
        this.gestorPlaylists = gp;
        this.gestorReproduccion = gr;
        this.gestorCompras = gcom;
        this.gestorEstadisticas = ge;

        setUndecorated(true);
        inicializarVentana();
        mostrarPantallaLogin();
    }

    // ============================================================
    //  INICIALIZACION
    // ============================================================
    private void inicializarVentana() {
        setTitle("MusicGO - Galeria Noir");
        setSize(1400, 900);
        setMinimumSize(new Dimension(1200, 800));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(NOIR_BLACK);
        setLayout(new BorderLayout(0, 0));

        // Header
        add(crearHeader(), BorderLayout.NORTH);

        // Panel central: Sidebar + Contenido
        JPanel panelContenido = new JPanel(new BorderLayout(0, 0));
        panelContenido.setBackground(NOIR_BLACK);

        // Sidebar
        panelSidebar = crearSidebar();
        panelContenido.add(panelSidebar, BorderLayout.WEST);

        // Panel central dinamico (aqui cambian las vistas)
        panelCentral = new JPanel(new BorderLayout());
        panelCentral.setBackground(NOIR_BLACK);
        panelContenido.add(panelCentral, BorderLayout.CENTER);

        add(panelContenido, BorderLayout.CENTER);

        // Reproductor
        panelReproductor = crearPanelReproductor();
        add(panelReproductor, BorderLayout.SOUTH);
    }

    // ============================================================
    //  HEADER
    // ============================================================
    private JPanel crearHeader() {
        JPanel header = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(NOIR_DARK);
                g2.drawLine(0, getHeight()-1, getWidth(), getHeight()-1);
            }
        };
        header.setBackground(NOIR_BLACK);
        header.setPreferredSize(new Dimension(0, 56));
        header.setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 24));

        // Izquierda
        JPanel izq = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 0));
        izq.setBackground(NOIR_BLACK);
        JButton btnMenu = crearBotonIcono("☰", 18);
        btnMenu.addActionListener(e -> toggleSidebar());
        izq.add(btnMenu);
        JLabel lblLogo = new JLabel("♪ MusicGO");
        lblLogo.setFont(new Font("Georgia", Font.BOLD, 20));
        lblLogo.setForeground(NOIR_BEIGE);
        izq.add(lblLogo);
        header.add(izq, BorderLayout.WEST);

        // Centro
        JLabel lblSeccion = new JLabel("GALERIA NOIR");
        lblSeccion.setFont(FONT_SMALL);
        lblSeccion.setForeground(NOIR_BROWN);
        lblSeccion.setHorizontalAlignment(SwingConstants.CENTER);
        header.add(lblSeccion, BorderLayout.CENTER);

        // Derecha
        JPanel der = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 0));
        der.setBackground(NOIR_BLACK);
        JTextField txtBuscar = new JTextField("Buscar...", 16);
        txtBuscar.setFont(FONT_SMALL);
        txtBuscar.setForeground(NOIR_GRAY);
        txtBuscar.setBackground(NOIR_DARK);
        txtBuscar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(NOIR_DARK, 1, true),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));
        txtBuscar.setCaretColor(NOIR_BEIGE);
        der.add(txtBuscar);
        JButton btnUser = crearBotonIcono("👤", 16);
        der.add(btnUser);
        header.add(der, BorderLayout.EAST);

        return header;
    }

    // ============================================================
    //  SIDEBAR CON BOTONES FUNCIONALES
    // ============================================================
    private JPanel crearSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(NOIR_DARK);
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, NOIR_BLACK));

        sidebar.add(Box.createVerticalStrut(24));

        // NAVEGACION
        sidebar.add(crearLabelSeccion("NAVEGACION"));
        sidebar.add(crearBotonSidebar("🏠  Inicio", () -> mostrarInicio()));
        sidebar.add(crearBotonSidebar("🔍  Explorar", () -> mostrarExplorar()));
        sidebar.add(crearBotonSidebar("📻  Radio", () -> mostrarRadio()));

        sidebar.add(Box.createVerticalStrut(24));

        // TU MUSICA
        sidebar.add(crearLabelSeccion("TU MUSICA"));
        sidebar.add(crearBotonSidebar("📂  Playlists", () -> mostrarPlaylists()));
        sidebar.add(crearBotonSidebar("🎵  Canciones", () -> mostrarCanciones()));
        sidebar.add(crearBotonSidebar("🎙️  Podcasts", () -> mostrarPodcasts()));
        sidebar.add(crearBotonSidebar("🛒  Tienda", () -> mostrarTienda()));

        sidebar.add(Box.createVerticalStrut(24));

        // PERFIL
        sidebar.add(crearLabelSeccion("PERFIL"));
        sidebar.add(crearBotonSidebar("📊  Estadisticas", () -> mostrarEstadisticas()));
        sidebar.add(crearBotonSidebar("👤  Usuarios", () -> mostrarUsuarios()));
        sidebar.add(crearBotonSidebar("💾  Guardar", () -> mostrarGuardar()));

        sidebar.add(Box.createVerticalGlue());

        // Usuario + Salir (vuelve al login, no cierra app)
        JPanel panelUser = new JPanel(new BorderLayout());
        panelUser.setBackground(NOIR_BLACK);
        panelUser.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, NOIR_GRAY),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)
        ));
        panelUser.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

        lblUsuarioSidebar = new JLabel("Sin sesion");
        lblUsuarioSidebar.setFont(FONT_SMALL);
        lblUsuarioSidebar.setForeground(NOIR_BEIGE_MED);
        panelUser.add(lblUsuarioSidebar, BorderLayout.CENTER);

        JButton btnCerrar = crearBotonTexto("Cerrar sesion", NOIR_RED);
        btnCerrar.addActionListener(e -> cerrarSesionYVolverAlLogin());
        panelUser.add(btnCerrar, BorderLayout.EAST);

        sidebar.add(panelUser);
        return sidebar;
    }

    private void toggleSidebar() {
        sidebarVisible = !sidebarVisible;
        panelSidebar.setVisible(sidebarVisible);
        revalidate();
        repaint();
    }

    private JLabel crearLabelSeccion(String texto) {
        JLabel lbl = new JLabel("  " + texto);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lbl.setForeground(NOIR_BROWN);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        lbl.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        return lbl;
    }

    private JButton crearBotonSidebar(String texto, Runnable accion) {
        JButton btn = new JButton(texto) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isRollover()) {
                    g2.setColor(new Color(60, 60, 60));
                    g2.fillRoundRect(12, 2, getWidth() - 24, getHeight() - 4, 8, 8);
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(FONT_BODY);
        btn.setForeground(NOIR_BEIGE_MED);
        btn.setBackground(NOIR_DARK);
        btn.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        btn.addActionListener(e -> accion.run());
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btn.setForeground(NOIR_BEIGE); }
            @Override public void mouseExited(MouseEvent e) { btn.setForeground(NOIR_BEIGE_MED); }
        });
        return btn;
    }

    // ============================================================
    //  VISTAS - METODOS DE NAVEGACION
    // ============================================================
    private void limpiarPanelCentral() {
        panelCentral.removeAll();
        panelCentral.revalidate();
        panelCentral.repaint();
    }

    private void mostrarInicio() {
        vistaActual = "inicio";
        limpiarPanelCentral();
        panelCentral.add(crearPanelInicio(), BorderLayout.CENTER);
        panelCentral.revalidate();
        panelCentral.repaint();
    }

    private void mostrarExplorar() {
        vistaActual = "explorar";
        limpiarPanelCentral();
        panelCentral.add(crearPanelExplorar(), BorderLayout.CENTER);
        panelCentral.revalidate();
        panelCentral.repaint();
    }

    private void mostrarRadio() {
        vistaActual = "radio";
        limpiarPanelCentral();
        panelCentral.add(crearPanelRadio(), BorderLayout.CENTER);
        panelCentral.revalidate();
        panelCentral.repaint();
    }

    private void mostrarPlaylists() {
        vistaActual = "playlists";
        limpiarPanelCentral();
        panelCentral.add(crearPanelPlaylists(), BorderLayout.CENTER);
        panelCentral.revalidate();
        panelCentral.repaint();
    }

    private void mostrarCanciones() {
        vistaActual = "canciones";
        limpiarPanelCentral();
        panelCentral.add(crearPanelCanciones(), BorderLayout.CENTER);
        panelCentral.revalidate();
        panelCentral.repaint();
    }

    private void mostrarPodcasts() {
        vistaActual = "podcasts";
        limpiarPanelCentral();
        panelCentral.add(crearPanelPodcasts(), BorderLayout.CENTER);
        panelCentral.revalidate();
        panelCentral.repaint();
    }

    private void mostrarTienda() {
        vistaActual = "tienda";
        limpiarPanelCentral();
        panelCentral.add(crearPanelTienda(), BorderLayout.CENTER);
        panelCentral.revalidate();
        panelCentral.repaint();
    }

    private void mostrarEstadisticas() {
        vistaActual = "estadisticas";
        limpiarPanelCentral();
        panelCentral.add(crearPanelEstadisticas(), BorderLayout.CENTER);
        panelCentral.revalidate();
        panelCentral.repaint();
    }

    private void mostrarUsuarios() {
        vistaActual = "usuarios";
        limpiarPanelCentral();
        panelCentral.add(crearPanelUsuarios(), BorderLayout.CENTER);
        panelCentral.revalidate();
        panelCentral.repaint();
    }

    private void mostrarGuardar() {
        vistaActual = "guardar";
        limpiarPanelCentral();
        panelCentral.add(crearPanelGuardar(), BorderLayout.CENTER);
        panelCentral.revalidate();
        panelCentral.repaint();
    }

    // ============================================================
    //  PANEL: INICIO
    // ============================================================
    private JScrollPane crearPanelInicio() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(NOIR_BLACK);
        panel.setBorder(BorderFactory.createEmptyBorder(24, 32, 24, 32));

        // Banner
        JPanel banner = new JPanel(new BorderLayout(24, 0)) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(60, 45, 30), getWidth(), getHeight(), NOIR_DARK);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
            }
        };
        banner.setBackground(new Color(0,0,0,0));
        banner.setBorder(BorderFactory.createEmptyBorder(32, 32, 32, 32));
        banner.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));
        banner.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblBienvenida = new JLabel("<html><b>Bienvenido a MusicGO</b><br><span style='font-size:14px;'>Descubre, escucha y disfruta tu musica favorita</span></html>");
        lblBienvenida.setFont(FONT_TITLE);
        lblBienvenida.setForeground(NOIR_BEIGE);
        banner.add(lblBienvenida, BorderLayout.CENTER);
        panel.add(banner);
        panel.add(Box.createVerticalStrut(32));

        // Secciones
        panel.add(crearTituloSeccion("Escuchado recientemente"));
        panel.add(Box.createVerticalStrut(16));
        JPanel gridReciente = new JPanel(new GridLayout(1, 4, 16, 0));
        gridReciente.setBackground(NOIR_BLACK);
        gridReciente.setAlignmentX(Component.LEFT_ALIGNMENT);
        gridReciente.setMaximumSize(new Dimension(Integer.MAX_VALUE, 260));
        for (int i = 0; i < 4; i++) {
            gridReciente.add(crearTarjetaPlaceholder("Cancion " + (i+1), "Artista " + (i+1), "3:45"));
        }
        panel.add(gridReciente);

        panel.add(Box.createVerticalStrut(32));
        panel.add(crearTituloSeccion("Recomendado para ti"));
        panel.add(Box.createVerticalStrut(16));
        JPanel gridRecomendado = new JPanel(new GridLayout(1, 5, 16, 0));
        gridRecomendado.setBackground(NOIR_BLACK);
        gridRecomendado.setAlignmentX(Component.LEFT_ALIGNMENT);
        gridRecomendado.setMaximumSize(new Dimension(Integer.MAX_VALUE, 260));
        for (int i = 0; i < 5; i++) {
            gridRecomendado.add(crearTarjetaPlaceholder("Album " + (i+1), "Artista Destacado", "12 canciones"));
        }
        panel.add(gridRecomendado);
        panel.add(Box.createVerticalGlue());

        JScrollPane scroll = new JScrollPane(panel);
        scroll.setBorder(null);
        scroll.setBackground(NOIR_BLACK);
        scroll.getViewport().setBackground(NOIR_BLACK);
        scroll.getVerticalScrollBar().setUI(new NoirScrollBarUI());
        scroll.getVerticalScrollBar().setBackground(NOIR_BLACK);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        return scroll;
    }

    // ============================================================
    //  PANEL: EXPLORAR (CON FILTROS FUNCIONALES)
    // ============================================================
    private JPanel crearPanelExplorar() {
        JPanel panel = new JPanel(new BorderLayout(0, 0));
        panel.setBackground(NOIR_BLACK);
        panel.setBorder(BorderFactory.createEmptyBorder(24, 32, 24, 32));

        // Tabs funcionales
        JPanel tabs = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        tabs.setBackground(NOIR_BLACK);

        JButton btnTodo = crearBotonTab("Todo", true);
        JButton btnCanciones = crearBotonTab("Canciones", false);
        JButton btnPodcasts = crearBotonTab("Podcasts", false);
        JButton btnProductos = crearBotonTab("Productos", false);

        // Contenedor del contenido filtrado
        JPanel contenidoPanel = new JPanel();
        contenidoPanel.setLayout(new BoxLayout(contenidoPanel, BoxLayout.Y_AXIS));
        contenidoPanel.setBackground(NOIR_BLACK);

        // Funcion para actualizar contenido segun filtro
        Runnable actualizarContenido = () -> {
            contenidoPanel.removeAll();
            contenidoPanel.add(Box.createVerticalStrut(16));

            if (btnTodo.isSelected() || btnCanciones.isSelected()) {
                contenidoPanel.add(crearTituloSeccion("Canciones"));
                contenidoPanel.add(Box.createVerticalStrut(16));
                JPanel gridCanciones = new JPanel(new GridLayout(0, 4, 16, 16));
                gridCanciones.setBackground(NOIR_BLACK);
                List<Audio> audios = gestorCatalogo.getTodosLosAudios();
                boolean hayCanciones = false;
                if (audios != null) {
                    for (Audio a : audios) {
                        if (a instanceof Cancion) {
                            Cancion c = (Cancion) a;
                            gridCanciones.add(crearTarjetaAudio(c.getTitulo(), c.getArtista(), c.duracionFormateada(), a));
                            hayCanciones = true;
                        }
                    }
                }
                if (!hayCanciones) {
                    gridCanciones.add(crearMensajeVacio("No hay canciones disponibles"));
                }
                contenidoPanel.add(gridCanciones);
                contenidoPanel.add(Box.createVerticalStrut(32));
            }

            if (btnTodo.isSelected() || btnPodcasts.isSelected()) {
                contenidoPanel.add(crearTituloSeccion("Podcasts"));
                contenidoPanel.add(Box.createVerticalStrut(16));
                JPanel gridPodcasts = new JPanel(new GridLayout(0, 4, 16, 16));
                gridPodcasts.setBackground(NOIR_BLACK);
                List<Audio> audios = gestorCatalogo.getTodosLosAudios();
                boolean hayPodcasts = false;
                if (audios != null) {
                    for (Audio a : audios) {
                        if (a instanceof EpisodioPodcast) {
                            EpisodioPodcast ep = (EpisodioPodcast) a;
                            gridPodcasts.add(crearTarjetaAudio(ep.getTitulo(), ep.getAnfitrion(), ep.duracionFormateada(), a));
                            hayPodcasts = true;
                        }
                    }
                }
                if (!hayPodcasts) {
                    gridPodcasts.add(crearMensajeVacio("No hay podcasts disponibles"));
                }
                contenidoPanel.add(gridPodcasts);
                contenidoPanel.add(Box.createVerticalStrut(32));
            }

            if (btnTodo.isSelected() || btnProductos.isSelected()) {
                contenidoPanel.add(crearTituloSeccion("Productos"));
                contenidoPanel.add(Box.createVerticalStrut(16));
                JPanel gridProductos = new JPanel(new GridLayout(0, 3, 16, 16));
                gridProductos.setBackground(NOIR_BLACK);
                List<Producto> productos = gestorCatalogo.getTodosLosProductos();
                if (productos != null && !productos.isEmpty()) {
                    for (Producto p : productos) {
                        gridProductos.add(crearTarjetaProducto(p));
                    }
                } else {
                    gridProductos.add(crearMensajeVacio("No hay productos disponibles"));
                }
                contenidoPanel.add(gridProductos);
            }

            contenidoPanel.add(Box.createVerticalGlue());
            contenidoPanel.revalidate();
            contenidoPanel.repaint();
        };

        // ActionListeners para tabs
        btnTodo.addActionListener(e -> {
            btnTodo.setSelected(true); btnCanciones.setSelected(false); btnPodcasts.setSelected(false); btnProductos.setSelected(false);
            actualizarContenido.run();
        });
        btnCanciones.addActionListener(e -> {
            btnTodo.setSelected(false); btnCanciones.setSelected(true); btnPodcasts.setSelected(false); btnProductos.setSelected(false);
            actualizarContenido.run();
        });
        btnPodcasts.addActionListener(e -> {
            btnTodo.setSelected(false); btnCanciones.setSelected(false); btnPodcasts.setSelected(true); btnProductos.setSelected(false);
            actualizarContenido.run();
        });
        btnProductos.addActionListener(e -> {
            btnTodo.setSelected(false); btnCanciones.setSelected(false); btnPodcasts.setSelected(false); btnProductos.setSelected(true);
            actualizarContenido.run();
        });

        tabs.add(btnTodo); tabs.add(btnCanciones); tabs.add(btnPodcasts); tabs.add(btnProductos);
        panel.add(tabs, BorderLayout.NORTH);

        // Mostrar todo inicialmente
        actualizarContenido.run();
        panel.add(envolverEnScroll(contenidoPanel), BorderLayout.CENTER);

        return panel;
    }

    // ============================================================
    //  PANEL: RADIO
    // ============================================================
    private JPanel crearPanelRadio() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(NOIR_BLACK);
        panel.setBorder(BorderFactory.createEmptyBorder(24, 32, 24, 32));

        JPanel inner = new JPanel();
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));
        inner.setBackground(NOIR_BLACK);

        JLabel lblIcon = new JLabel("📻");
        lblIcon.setFont(new Font("Segoe UI", Font.PLAIN, 64));
        lblIcon.setAlignmentX(Component.CENTER_ALIGNMENT);
        inner.add(lblIcon);
        inner.add(Box.createVerticalStrut(16));

        JLabel lblTitulo = new JLabel("Radio MusicGO");
        lblTitulo.setFont(FONT_TITLE);
        lblTitulo.setForeground(NOIR_BEIGE);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        inner.add(lblTitulo);

        inner.add(Box.createVerticalStrut(8));
        JLabel lblSub = new JLabel("Reproduccion aleatoria de tu catalogo");
        lblSub.setFont(FONT_BODY);
        lblSub.setForeground(NOIR_BROWN);
        lblSub.setAlignmentX(Component.CENTER_ALIGNMENT);
        inner.add(lblSub);

        inner.add(Box.createVerticalStrut(24));
        JButton btnPlayRadio = crearBotonNoir("▶ Iniciar Radio");
        btnPlayRadio.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnPlayRadio.addActionListener(e -> {
            List<Audio> audios = gestorCatalogo.getTodosLosAudios();
            if (audios != null && !audios.isEmpty()) {
                int random = (int)(Math.random() * audios.size());
                Audio a = audios.get(random);
                if (a instanceof Cancion) reproducirCancion((Cancion)a);
                else if (a instanceof EpisodioPodcast) reproducirPodcast((EpisodioPodcast)a);
            }
        });
        inner.add(btnPlayRadio);

        panel.add(inner);
        return panel;
    }

    // ============================================================
    //  PANEL: PLAYLISTS (UNICA VENTANA - CORREGIDO)
    // ============================================================
    private JPanel crearPanelPlaylists() {
        JPanel panel = new JPanel(new BorderLayout(0, 0));
        panel.setBackground(NOIR_BLACK);
        panel.setBorder(BorderFactory.createEmptyBorder(24, 32, 24, 32));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(NOIR_BLACK);
        JLabel lblTitulo = new JLabel("Tus Playlists");
        lblTitulo.setFont(FONT_TITLE);
        lblTitulo.setForeground(NOIR_BEIGE);
        header.add(lblTitulo, BorderLayout.WEST);

        JButton btnCrear = crearBotonNoir("+ Nueva Playlist");
        btnCrear.addActionListener(e -> mostrarDialogoCrearPlaylist());
        header.add(btnCrear, BorderLayout.EAST);
        panel.add(header, BorderLayout.NORTH);

        // Contenido
        JPanel contenido = new JPanel();
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        contenido.setBackground(NOIR_BLACK);
        contenido.add(Box.createVerticalStrut(24));

        if (usuarioActual != null && usuarioActual.getBiblioteca() != null) {
            List<Playlist> playlists = usuarioActual.getBiblioteca().getPlaylists();
            if (playlists != null && !playlists.isEmpty()) {
                JPanel grid = new JPanel(new GridLayout(0, 3, 16, 16));
                grid.setBackground(NOIR_BLACK);
                for (Playlist p : playlists) {
                    grid.add(crearTarjetaPlaylistClickable(p));
                }
                contenido.add(grid);
            } else {
                contenido.add(crearMensajeVacioConSub("No tienes playlists", "Crea tu primera playlist"));
            }
        } else {
            contenido.add(crearMensajeVacioConSub("Inicia sesion", "para ver tus playlists"));
        }

        contenido.add(Box.createVerticalGlue());
        panel.add(envolverEnScroll(contenido), BorderLayout.CENTER);
        return panel;
    }

    // Tarjeta de playlist que al hacer click muestra su contenido
    private JPanel crearTarjetaPlaylistClickable(Playlist p) {
        JPanel tarjeta = new JPanel(new BorderLayout(12, 0)) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(NOIR_DARK);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
            }
        };
        tarjeta.setBackground(new Color(0,0,0,0));
        tarjeta.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        tarjeta.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        tarjeta.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Icono vinilo
        JLabel lblIcon = new JLabel("◉") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(NOIR_BROWN);
                g2.fillOval(0, 0, 48, 48);
                g2.setColor(NOIR_BLACK);
                g2.fillOval(16, 16, 16, 16);
                super.paintComponent(g);
            }
        };
        lblIcon.setPreferredSize(new Dimension(48, 48));
        lblIcon.setHorizontalAlignment(SwingConstants.CENTER);
        lblIcon.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        lblIcon.setForeground(NOIR_BEIGE);
        tarjeta.add(lblIcon, BorderLayout.WEST);

        // Info
        JPanel info = new JPanel(new GridLayout(2, 1, 0, 4));
        info.setBackground(new Color(0,0,0,0));
        JLabel lblNombre = new JLabel(p.getNombre());
        lblNombre.setFont(FONT_BODY);
        lblNombre.setForeground(NOIR_BEIGE);
        info.add(lblNombre);
        JLabel lblCount = new JLabel(p.getContenido().size() + " canciones");
        lblCount.setFont(FONT_SMALL);
        lblCount.setForeground(NOIR_BROWN);
        info.add(lblCount);
        tarjeta.add(info, BorderLayout.CENTER);

        // Click para ver contenido
        tarjeta.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                mostrarContenidoPlaylist(p);
            }
        });

        return tarjeta;
    }

    // Dialogo para mostrar contenido de una playlist
    private void mostrarContenidoPlaylist(Playlist p) {
        JDialog dialog = new JDialog(this, "Playlist: " + p.getNombre(), true);
        dialog.setSize(600, 500);
        dialog.setLocationRelativeTo(this);
        dialog.getRootPane().setBorder(BorderFactory.createLineBorder(NOIR_BROWN, 1));

        JPanel panel = new JPanel(new BorderLayout(0, 0));
        panel.setBackground(NOIR_BLACK);
        panel.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(NOIR_BLACK);
        JLabel lblTitulo = new JLabel(p.getNombre());
        lblTitulo.setFont(FONT_TITLE);
        lblTitulo.setForeground(NOIR_BEIGE);
        header.add(lblTitulo, BorderLayout.WEST);

        JButton btnAgregar = crearBotonNoir("+ Agregar cancion");
        btnAgregar.addActionListener(e -> mostrarDialogoAgregarCancionAPlaylist(p));
        header.add(btnAgregar, BorderLayout.EAST);
        panel.add(header, BorderLayout.NORTH);

        // Lista de canciones
        JPanel lista = new JPanel();
        lista.setLayout(new BoxLayout(lista, BoxLayout.Y_AXIS));
        lista.setBackground(NOIR_BLACK);
        lista.add(Box.createVerticalStrut(16));

        if (p.getContenido() != null && !p.getContenido().isEmpty()) {
            int num = 1;
            for (Audio a : p.getContenido()) {
                lista.add(crearFilaAudioEnPlaylist(num++, a, p));
                lista.add(Box.createVerticalStrut(4));
            }
        } else {
            lista.add(crearMensajeVacioConSub("Playlist vacia", "Agrega canciones desde el catalogo"));
        }

        lista.add(Box.createVerticalGlue());
        panel.add(envolverEnScroll(lista), BorderLayout.CENTER);

        // Boton cerrar
        JButton btnCerrar = crearBotonNoir("Cerrar");
        btnCerrar.addActionListener(e -> dialog.dispose());
        JPanel panelBtn = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelBtn.setBackground(NOIR_BLACK);
        panelBtn.add(btnCerrar);
        panel.add(panelBtn, BorderLayout.SOUTH);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private JPanel crearFilaAudioEnPlaylist(int num, Audio a, Playlist p) {
        JPanel fila = new JPanel(new BorderLayout(16, 0));
        fila.setBackground(NOIR_BLACK);
        fila.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));
        fila.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel lblNum = new JLabel(String.format("%02d", num));
        lblNum.setFont(FONT_MONO);
        lblNum.setForeground(NOIR_GRAY);
        fila.add(lblNum, BorderLayout.WEST);

        JPanel info = new JPanel(new GridLayout(2, 1, 0, 2));
        info.setBackground(NOIR_BLACK);
        JLabel lblTitulo = new JLabel(a.getTitulo());
        lblTitulo.setFont(FONT_BODY);
        lblTitulo.setForeground(NOIR_BEIGE);
        info.add(lblTitulo);

        String subtitulo = "Audio";
        if (a instanceof Cancion) subtitulo = ((Cancion)a).getArtista() + " • " + ((Cancion)a).getAlbum();
        else if (a instanceof EpisodioPodcast) subtitulo = ((EpisodioPodcast)a).getAnfitrion();
        JLabel lblSub = new JLabel(subtitulo);
        lblSub.setFont(FONT_SMALL);
        lblSub.setForeground(NOIR_BROWN);
        info.add(lblSub);
        fila.add(info, BorderLayout.CENTER);

        JLabel lblDur = new JLabel(a.duracionFormateada());
        lblDur.setFont(FONT_MONO);
        lblDur.setForeground(NOIR_GRAY);
        fila.add(lblDur, BorderLayout.EAST);

        fila.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { fila.setBackground(NOIR_DARK); info.setBackground(NOIR_DARK); }
            @Override public void mouseExited(MouseEvent e) { fila.setBackground(NOIR_BLACK); info.setBackground(NOIR_BLACK); }
            @Override public void mouseClicked(MouseEvent e) {
                if (a instanceof Cancion) reproducirCancion((Cancion)a);
                else if (a instanceof EpisodioPodcast) reproducirPodcast((EpisodioPodcast)a);
            }
        });

        return fila;
    }

    // ============================================================
    //  PANEL: CANCIONES
    // ============================================================
    private JPanel crearPanelCanciones() {
        JPanel panel = new JPanel(new BorderLayout(0, 0));
        panel.setBackground(NOIR_BLACK);
        panel.setBorder(BorderFactory.createEmptyBorder(24, 32, 24, 32));

        JLabel lblTitulo = new JLabel("Todas las Canciones");
        lblTitulo.setFont(FONT_TITLE);
        lblTitulo.setForeground(NOIR_BEIGE);
        panel.add(lblTitulo, BorderLayout.NORTH);

        JPanel contenido = new JPanel();
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        contenido.setBackground(NOIR_BLACK);
        contenido.add(Box.createVerticalStrut(24));

        List<Audio> audios = gestorCatalogo.getTodosLosAudios();
        if (audios != null) {
            int num = 1;
            for (Audio a : audios) {
                if (a instanceof Cancion) {
                    contenido.add(crearFilaCancion(num++, (Cancion) a));
                    contenido.add(Box.createVerticalStrut(4));
                }
            }
        }

        contenido.add(Box.createVerticalGlue());
        panel.add(envolverEnScroll(contenido), BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearFilaCancion(int num, Cancion c) {
        JPanel fila = new JPanel(new BorderLayout(16, 0));
        fila.setBackground(NOIR_BLACK);
        fila.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));
        fila.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel lblNum = new JLabel(String.format("%02d", num));
        lblNum.setFont(FONT_MONO);
        lblNum.setForeground(NOIR_GRAY);
        fila.add(lblNum, BorderLayout.WEST);

        JPanel info = new JPanel(new GridLayout(2, 1, 0, 2));
        info.setBackground(NOIR_BLACK);
        JLabel lblTitulo = new JLabel(c.getTitulo());
        lblTitulo.setFont(FONT_BODY);
        lblTitulo.setForeground(NOIR_BEIGE);
        info.add(lblTitulo);
        JLabel lblArtista = new JLabel(c.getArtista() + "  •  " + c.getAlbum());
        lblArtista.setFont(FONT_SMALL);
        lblArtista.setForeground(NOIR_BROWN);
        info.add(lblArtista);
        fila.add(info, BorderLayout.CENTER);

        JLabel lblDur = new JLabel(c.duracionFormateada());
        lblDur.setFont(FONT_MONO);
        lblDur.setForeground(NOIR_GRAY);
        fila.add(lblDur, BorderLayout.EAST);

        fila.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { fila.setBackground(NOIR_DARK); info.setBackground(NOIR_DARK); }
            @Override public void mouseExited(MouseEvent e) { fila.setBackground(NOIR_BLACK); info.setBackground(NOIR_BLACK); }
            @Override public void mouseClicked(MouseEvent e) { reproducirCancion(c); }
        });

        return fila;
    }

    // ============================================================
    //  PANEL: PODCASTS
    // ============================================================
    private JPanel crearPanelPodcasts() {
        JPanel panel = new JPanel(new BorderLayout(0, 0));
        panel.setBackground(NOIR_BLACK);
        panel.setBorder(BorderFactory.createEmptyBorder(24, 32, 24, 32));

        JLabel lblTitulo = new JLabel("Podcasts");
        lblTitulo.setFont(FONT_TITLE);
        lblTitulo.setForeground(NOIR_BEIGE);
        panel.add(lblTitulo, BorderLayout.NORTH);

        JPanel contenido = new JPanel();
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        contenido.setBackground(NOIR_BLACK);
        contenido.add(Box.createVerticalStrut(24));

        List<Audio> audios = gestorCatalogo.getTodosLosAudios();
        if (audios != null) {
            int num = 1;
            for (Audio a : audios) {
                if (a instanceof EpisodioPodcast) {
                    contenido.add(crearFilaPodcast(num++, (EpisodioPodcast) a));
                    contenido.add(Box.createVerticalStrut(4));
                }
            }
        }

        contenido.add(Box.createVerticalGlue());
        panel.add(envolverEnScroll(contenido), BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearFilaPodcast(int num, EpisodioPodcast ep) {
        JPanel fila = new JPanel(new BorderLayout(16, 0));
        fila.setBackground(NOIR_BLACK);
        fila.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));
        fila.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel lblNum = new JLabel(String.format("%02d", num));
        lblNum.setFont(FONT_MONO);
        lblNum.setForeground(NOIR_GRAY);
        fila.add(lblNum, BorderLayout.WEST);

        JPanel info = new JPanel(new GridLayout(2, 1, 0, 2));
        info.setBackground(NOIR_BLACK);
        JLabel lblTitulo = new JLabel(ep.getTitulo());
        lblTitulo.setFont(FONT_BODY);
        lblTitulo.setForeground(NOIR_BEIGE);
        info.add(lblTitulo);
        JLabel lblPodcast = new JLabel(ep.getNombrePodcast() + "  •  " + ep.getAnfitrion());
        lblPodcast.setFont(FONT_SMALL);
        lblPodcast.setForeground(NOIR_BROWN);
        info.add(lblPodcast);
        fila.add(info, BorderLayout.CENTER);

        JLabel lblDur = new JLabel(ep.duracionFormateada());
        lblDur.setFont(FONT_MONO);
        lblDur.setForeground(NOIR_GRAY);
        fila.add(lblDur, BorderLayout.EAST);

        fila.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { fila.setBackground(NOIR_DARK); info.setBackground(NOIR_DARK); }
            @Override public void mouseExited(MouseEvent e) { fila.setBackground(NOIR_BLACK); info.setBackground(NOIR_BLACK); }
            @Override public void mouseClicked(MouseEvent e) { reproducirPodcast(ep); }
        });

        return fila;
    }

    // ============================================================
    //  PANEL: TIENDA
    // ============================================================
    private JPanel crearPanelTienda() {
        JPanel panel = new JPanel(new BorderLayout(0, 0));
        panel.setBackground(NOIR_BLACK);
        panel.setBorder(BorderFactory.createEmptyBorder(24, 32, 24, 32));

        JLabel lblTitulo = new JLabel("Tienda MusicGO");
        lblTitulo.setFont(FONT_TITLE);
        lblTitulo.setForeground(NOIR_BEIGE);
        panel.add(lblTitulo, BorderLayout.NORTH);

        JPanel contenido = new JPanel();
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        contenido.setBackground(NOIR_BLACK);
        contenido.add(Box.createVerticalStrut(24));

        List<Producto> productos = gestorCatalogo.getTodosLosProductos();
        if (productos != null && !productos.isEmpty()) {
            JPanel grid = new JPanel(new GridLayout(0, 3, 16, 16));
            grid.setBackground(NOIR_BLACK);
            for (Producto p : productos) {
                grid.add(crearTarjetaProducto(p));
            }
            contenido.add(grid);
        } else {
            contenido.add(crearMensajeVacioConSub("No hay productos", "Vuelve mas tarde"));
        }

        contenido.add(Box.createVerticalGlue());
        panel.add(envolverEnScroll(contenido), BorderLayout.CENTER);
        return panel;
    }

    // ============================================================
    //  PANEL: ESTADISTICAS
    // ============================================================
    private JPanel crearPanelEstadisticas() {
        JPanel panel = new JPanel(new BorderLayout(0, 0));
        panel.setBackground(NOIR_BLACK);
        panel.setBorder(BorderFactory.createEmptyBorder(24, 32, 24, 32));

        JLabel lblTitulo = new JLabel("Tus Estadisticas");
        lblTitulo.setFont(FONT_TITLE);
        lblTitulo.setForeground(NOIR_BEIGE);
        panel.add(lblTitulo, BorderLayout.NORTH);

        JPanel contenido = new JPanel();
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        contenido.setBackground(NOIR_BLACK);
        contenido.add(Box.createVerticalStrut(24));

        if (usuarioActual != null) {
            Estadisticas stats = usuarioActual.getEstadisticas();

            JPanel gridStats = new JPanel(new GridLayout(2, 2, 16, 16));
            gridStats.setBackground(NOIR_BLACK);
            gridStats.setMaximumSize(new Dimension(Integer.MAX_VALUE, 280));
            gridStats.setAlignmentX(Component.LEFT_ALIGNMENT);

            gridStats.add(crearTarjetaEstadistica("Tiempo de escucha", stats.tiempoFormateado(), NOIR_BROWN));
            gridStats.add(crearTarjetaEstadistica("Reproducciones", String.valueOf(stats.getReproduccionesTotales()), NOIR_BEIGE_MED));
            gridStats.add(crearTarjetaEstadistica("Canciones en biblioteca", String.valueOf(stats.getCancionesEnBiblioteca()), NOIR_BEIGE));
            gridStats.add(crearTarjetaEstadistica("Compras realizadas", String.valueOf(stats.getComprasRealizadas()), NOIR_RED));

            contenido.add(gridStats);
            contenido.add(Box.createVerticalStrut(32));

            JPanel panelMensaje = new JPanel();
            panelMensaje.setBackground(NOIR_DARK);
            panelMensaje.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(NOIR_BROWN, 2, true),
                    BorderFactory.createEmptyBorder(24, 24, 24, 24)
            ));
            panelMensaje.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
            panelMensaje.setAlignmentX(Component.LEFT_ALIGNMENT);

            JLabel lblMensaje = new JLabel();
            lblMensaje.setFont(FONT_SUBTITLE);
            lblMensaje.setForeground(NOIR_BEIGE);

            if (stats.getReproduccionesTotales() > 100) {
                lblMensaje.setText("Eres un oyente nivel Leyenda!");
            } else if (stats.getReproduccionesTotales() > 0) {
                lblMensaje.setText("Sigue descubriendo nueva musica!");
            } else {
                lblMensaje.setText("Tu historial esta vacio. Empieza a escuchar ahora!");
            }

            panelMensaje.add(lblMensaje);
            contenido.add(panelMensaje);
        } else {
            contenido.add(crearMensajeVacioConSub("Inicia sesion", "para ver tus estadisticas"));
        }

        contenido.add(Box.createVerticalGlue());
        panel.add(envolverEnScroll(contenido), BorderLayout.CENTER);
        return panel;
    }

    // ============================================================
    //  PANEL: USUARIOS (ADMIN)
    // ============================================================
    private JPanel crearPanelUsuarios() {
        JPanel panel = new JPanel(new BorderLayout(0, 0));
        panel.setBackground(NOIR_BLACK);
        panel.setBorder(BorderFactory.createEmptyBorder(24, 32, 24, 32));

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(NOIR_BLACK);
        JLabel lblTitulo = new JLabel("Gestion de Usuarios");
        lblTitulo.setFont(FONT_TITLE);
        lblTitulo.setForeground(NOIR_BEIGE);
        header.add(lblTitulo, BorderLayout.WEST);

        JButton btnRecargar = crearBotonNoir("Recargar");
        btnRecargar.addActionListener(e -> {
            mostrarUsuarios();
        });
        header.add(btnRecargar, BorderLayout.EAST);
        panel.add(header, BorderLayout.NORTH);

        String[] columnas = {"ID", "Nombre/Alias", "Correo", "Playlists", "Compras"};
        List<Usuario> usuarios = gestorUsuarios.getUsuarios();

        Object[][] datos;
        if (usuarios != null && !usuarios.isEmpty()) {
            datos = new Object[usuarios.size()][5];
            for (int i = 0; i < usuarios.size(); i++) {
                Usuario u = usuarios.get(i);
                datos[i][0] = u.getId();
                datos[i][1] = u.getNombre();
                datos[i][2] = u.getCorreo();
                datos[i][3] = u.getBiblioteca() != null ? u.getBiblioteca().cantidadPlaylists() : 0;
                datos[i][4] = u.getHistorialCompras() != null ? u.getHistorialCompras().size() : 0;
            }
        } else {
            datos = new Object[0][5];
        }

        JTable tabla = new JTable(datos, columnas) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tabla.setBackground(NOIR_BLACK);
        tabla.setForeground(NOIR_BEIGE);
        tabla.setGridColor(NOIR_DARK);
        tabla.setSelectionBackground(NOIR_DARK);
        tabla.setSelectionForeground(NOIR_BEIGE);
        tabla.setFont(FONT_BODY);
        tabla.setRowHeight(44);
        tabla.setShowGrid(false);
        tabla.setIntercellSpacing(new Dimension(0, 0));
        tabla.setFillsViewportHeight(true);
        tabla.getTableHeader().setBackground(NOIR_DARK);
        tabla.getTableHeader().setForeground(NOIR_BROWN);
        tabla.getTableHeader().setFont(FONT_SMALL);
        tabla.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, NOIR_DARK));

        JScrollPane scrollTabla = new JScrollPane(tabla);
        scrollTabla.setBorder(BorderFactory.createEmptyBorder(16, 0, 0, 0));
        scrollTabla.setBackground(NOIR_BLACK);
        scrollTabla.getViewport().setBackground(NOIR_BLACK);
        scrollTabla.getVerticalScrollBar().setUI(new NoirScrollBarUI());

        panel.add(scrollTabla, BorderLayout.CENTER);
        return panel;
    }

    // ============================================================
    //  PANEL: GUARDAR
    // ============================================================
    private JPanel crearPanelGuardar() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(NOIR_BLACK);
        panel.setBorder(BorderFactory.createEmptyBorder(24, 32, 24, 32));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblTitulo = new JLabel("Guardar Datos");
        lblTitulo.setFont(FONT_TITLE);
        lblTitulo.setForeground(NOIR_BEIGE);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(lblTitulo, gbc);

        gbc.gridy = 1;
        JLabel lblDesc = new JLabel("Guarda todos los cambios en la base de datos");
        lblDesc.setFont(FONT_BODY);
        lblDesc.setForeground(NOIR_BROWN);
        panel.add(lblDesc, gbc);

        gbc.gridy = 2; gbc.gridwidth = 1;
        JButton btnGuardar = crearBotonNoir("Guardar Cambios");
        btnGuardar.addActionListener(e -> {
            gestorUsuarios.guardarCambios();
            JOptionPane.showMessageDialog(this, "Datos guardados exitosamente", "MusicGO", JOptionPane.INFORMATION_MESSAGE);
        });
        panel.add(btnGuardar, gbc);

        return panel;
    }

    // ============================================================
    //  REPRODUCTOR CON VINILO
    // ============================================================
    private JPanel crearPanelReproductor() {
        JPanel panel = new JPanel(new BorderLayout(24, 0));
        panel.setBackground(NOIR_DARK);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, NOIR_BLACK),
                BorderFactory.createEmptyBorder(16, 24, 16, 24)
        ));
        panel.setPreferredSize(new Dimension(0, 200));

        // Vinilo animado
        panelVinilo = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int cx = getWidth() / 2, cy = getHeight() / 2;
                int r = Math.min(cx, cy) - 8;

                g2.setColor(NOIR_BLACK);
                g2.fillOval(cx - r, cy - r, r * 2, r * 2);
                g2.setColor(new Color(30, 30, 30));
                for (int i = r - 8; i > 20; i -= 4) g2.drawOval(cx - i, cy - i, i * 2, i * 2);

                if (reproduciendo) g2.rotate(anguloVinilo, cx, cy);
                g2.setColor(NOIR_BROWN);
                g2.fillOval(cx - 18, cy - 18, 36, 36);
                g2.setColor(NOIR_BLACK);
                g2.fillOval(cx - 6, cy - 6, 12, 12);
            }
        };
        panelVinilo.setBackground(NOIR_DARK);
        panelVinilo.setPreferredSize(new Dimension(160, 160));
        panel.add(panelVinilo, BorderLayout.WEST);

        // Centro: Info + Controles
        JPanel centro = new JPanel();
        centro.setLayout(new BoxLayout(centro, BoxLayout.Y_AXIS));
        centro.setBackground(NOIR_DARK);

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(NOIR_DARK);
        infoPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblInfoCancion = new JLabel("Selecciona una cancion");
        lblInfoCancion.setFont(FONT_TITLE);
        lblInfoCancion.setForeground(NOIR_BEIGE);
        lblInfoCancion.setAlignmentX(Component.CENTER_ALIGNMENT);
        infoPanel.add(lblInfoCancion);

        lblInfoArtista = new JLabel("MusicGO - Galeria Noir");
        lblInfoArtista.setFont(FONT_SUBTITLE);
        lblInfoArtista.setForeground(NOIR_BROWN);
        lblInfoArtista.setAlignmentX(Component.CENTER_ALIGNMENT);
        infoPanel.add(lblInfoArtista);
        centro.add(infoPanel);
        centro.add(Box.createVerticalStrut(16));

        // Controles
        JPanel controles = new JPanel(new FlowLayout(FlowLayout.CENTER, 24, 0));
        controles.setBackground(NOIR_DARK);
        JButton btnPrev = crearBotonControl("⏮", 18);
        btnPlay = crearBotonControl("▶", 24);
        JButton btnNext = crearBotonControl("⏭", 18);
        btnPlay.addActionListener(e -> toggleReproduccion());
        controles.add(btnPrev); controles.add(btnPlay); controles.add(btnNext);
        centro.add(controles);
        centro.add(Box.createVerticalStrut(12));

        // Progress bar
        progressBar = new JProgressBar(0, 100) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth(), h = getHeight();
                g2.setColor(NOIR_BLACK);
                g2.fillRoundRect(0, h/2 - 2, w, 4, 4, 4);
                int pw = (int)((getValue() / (double)getMaximum()) * w);
                g2.setColor(NOIR_BROWN);
                g2.fillRoundRect(0, h/2 - 2, pw, 4, 4, 4);
                g2.dispose();
            }
        };
        progressBar.setValue(0);
        progressBar.setBackground(NOIR_DARK);
        progressBar.setForeground(NOIR_BROWN);
        progressBar.setBorder(null);
        progressBar.setMaximumSize(new Dimension(400, 8));
        progressBar.setPreferredSize(new Dimension(400, 8));
        centro.add(progressBar);

        panel.add(centro, BorderLayout.CENTER);

        // Volumen
        JPanel der = new JPanel();
        der.setLayout(new BoxLayout(der, BoxLayout.Y_AXIS));
        der.setBackground(NOIR_DARK);
        JLabel lblVol = new JLabel("🔊");
        lblVol.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblVol.setForeground(NOIR_BEIGE_MED);
        lblVol.setAlignmentX(Component.CENTER_ALIGNMENT);
        der.add(lblVol);
        JSlider slider = new JSlider(0, 100, 70);
        slider.setBackground(NOIR_DARK);
        slider.setForeground(NOIR_BROWN);
        slider.setPreferredSize(new Dimension(80, 20));
        der.add(slider);
        panel.add(der, BorderLayout.EAST);

        // Timer vinilo
        viniloTimer = new Timer(50, e -> {
            if (reproduciendo) { anguloVinilo += 0.05; panelVinilo.repaint(); }
        });
        viniloTimer.start();

        return panel;
    }

    // ============================================================
    //  LOGIN CINEMATICO
    // ============================================================
    private void mostrarPantallaLogin() {
        JDialog dialog = new JDialog(this, "MusicGO", true);
        dialog.setSize(520, 640);
        dialog.setLocationRelativeTo(this);
        dialog.setUndecorated(true);
        dialog.getRootPane().setBorder(BorderFactory.createLineBorder(NOIR_BROWN, 1));

        JPanel panel = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, NOIR_BLACK, getWidth(), getHeight(), new Color(25, 20, 15));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(NOIR_BROWN);
                g2.setStroke(new BasicStroke(1));
                g2.drawLine(40, 120, getWidth() - 40, 120);
                g2.drawLine(40, getHeight() - 120, getWidth() - 40, getHeight() - 120);
            }
        };
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(NOIR_BLACK);
        panel.setBorder(BorderFactory.createEmptyBorder(60, 60, 60, 60));

        // Logo
        JLabel lblLogo = new JLabel("♪") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(NOIR_BROWN);
                g2.setStroke(new BasicStroke(2));
                int cx = getWidth() / 2, cy = getHeight() / 2;
                g2.drawOval(cx - 28, cy - 28, 56, 56);
                g2.drawOval(cx - 20, cy - 20, 40, 40);
                super.paintComponent(g);
            }
        };
        lblLogo.setFont(new Font("Georgia", Font.BOLD, 36));
        lblLogo.setForeground(NOIR_BEIGE);
        lblLogo.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblLogo.setPreferredSize(new Dimension(80, 80));
        panel.add(lblLogo);
        panel.add(Box.createVerticalStrut(8));

        JLabel lblTitulo = new JLabel("MusicGO");
        lblTitulo.setFont(FONT_DISPLAY);
        lblTitulo.setForeground(NOIR_BEIGE);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(lblTitulo);

        JLabel lblSub = new JLabel("Galeria Noir");
        lblSub.setFont(FONT_SUBTITLE);
        lblSub.setForeground(NOIR_BROWN);
        lblSub.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(lblSub);
        panel.add(Box.createVerticalStrut(40));

        // Campos
        panel.add(crearLabelCampo("Usuario"));
        panel.add(Box.createVerticalStrut(4));
        JTextField txtUsuario = crearCampoNoir();
        txtUsuario.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtUsuario.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        panel.add(txtUsuario);
        panel.add(Box.createVerticalStrut(20));

        panel.add(crearLabelCampo("Correo Gmail"));
        panel.add(Box.createVerticalStrut(4));
        JTextField txtCorreo = crearCampoNoir();
        txtCorreo.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtCorreo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        panel.add(txtCorreo);
        panel.add(Box.createVerticalStrut(32));

        // Boton ingresar
        JButton btnIngresar = crearBotonNoir("Ingresar");
        btnIngresar.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnIngresar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        btnIngresar.addActionListener(e -> {
            String alias = txtUsuario.getText().trim();
            String correo = txtCorreo.getText().trim();

            if (alias.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Ingresa tu usuario", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Usuario u = gestorUsuarios.buscarPorIdOAlias(alias);

            if (u == null) {
                if (!Validadores.esGmailValido(correo)) {
                    JOptionPane.showMessageDialog(dialog, "Ingresa un correo Gmail valido", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                try {
                    gestorUsuarios.registrar(alias, correo);
                    u = gestorUsuarios.buscarPorIdOAlias(alias);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } else {
                if (!u.getCorreo().equalsIgnoreCase(correo)) {
                    JOptionPane.showMessageDialog(dialog, "El correo no coincide", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            usuarioActual = u;
            lblUsuarioSidebar.setText(u.getNombre());
            dialog.dispose();
            mostrarInicio();
        });
        panel.add(btnIngresar);
        panel.add(Box.createVerticalStrut(16));

        // Boton salir (cierra app completamente desde login)
        JButton btnSalir = new JButton("Salir del sistema") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(NOIR_DARK);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 24, 24);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnSalir.setFont(FONT_BUTTON);
        btnSalir.setForeground(NOIR_BEIGE_MED);
        btnSalir.setBorder(BorderFactory.createEmptyBorder(14, 0, 14, 0));
        btnSalir.setFocusPainted(false);
        btnSalir.setContentAreaFilled(false);
        btnSalir.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSalir.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnSalir.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        btnSalir.addActionListener(e -> System.exit(0));
        panel.add(btnSalir);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private JLabel crearLabelCampo(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(FONT_SMALL);
        lbl.setForeground(NOIR_BEIGE_MED);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private JTextField crearCampoNoir() {
        JTextField txt = new JTextField();
        txt.setFont(FONT_BODY);
        txt.setForeground(NOIR_BEIGE);
        txt.setBackground(NOIR_DARK);
        txt.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(NOIR_GRAY, 1, true),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)
        ));
        txt.setCaretColor(NOIR_BEIGE);
        txt.setSelectionColor(NOIR_BROWN);
        txt.setSelectedTextColor(NOIR_BLACK);
        return txt;
    }

    // ============================================================
    //  FUNCIONALIDADES DE REPRODUCCION
    // ============================================================
    private void reproducirCancion(Cancion c) {
        if (usuarioActual == null) {
            JOptionPane.showMessageDialog(this, "Inicia sesion para reproducir", "MusicGO", JOptionPane.WARNING_MESSAGE);
            return;
        }
        audioReproduciendo = c;
        reproduciendo = true;
        segundosReproduccion = 0;
        lblInfoCancion.setText(c.getTitulo());
        lblInfoArtista.setText(c.getArtista() + "  •  " + c.getAlbum());
        btnPlay.setText("⏸");
        progressBar.setMaximum(c.getDuracionSegundos());
        progressBar.setValue(0);

        if (timerReproduccion != null) timerReproduccion.stop();
        timerReproduccion = new Timer(1000, e -> {
            segundosReproduccion++;
            progressBar.setValue(segundosReproduccion);
            if (segundosReproduccion >= c.getDuracionSegundos()) {
                timerReproduccion.stop();
                reproduciendo = false;
                btnPlay.setText("▶");
            }
        });
        timerReproduccion.start();

        try { gestorReproduccion.reproducir(usuarioActual, c.getId()); }
        catch (ContenidoNoEncontradoException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void reproducirPodcast(EpisodioPodcast ep) {
        if (usuarioActual == null) {
            JOptionPane.showMessageDialog(this, "Inicia sesion para reproducir", "MusicGO", JOptionPane.WARNING_MESSAGE);
            return;
        }
        audioReproduciendo = ep;
        reproduciendo = true;
        segundosReproduccion = 0;
        lblInfoCancion.setText(ep.getTitulo());
        lblInfoArtista.setText(ep.getNombrePodcast() + "  •  " + ep.getAnfitrion());
        btnPlay.setText("⏸");
        progressBar.setMaximum(ep.getDuracionSegundos());
        progressBar.setValue(0);

        if (timerReproduccion != null) timerReproduccion.stop();
        timerReproduccion = new Timer(1000, e -> {
            segundosReproduccion++;
            progressBar.setValue(segundosReproduccion);
            if (segundosReproduccion >= ep.getDuracionSegundos()) {
                timerReproduccion.stop();
                reproduciendo = false;
                btnPlay.setText("▶");
            }
        });
        timerReproduccion.start();

        try { gestorReproduccion.reproducir(usuarioActual, ep.getId()); }
        catch (ContenidoNoEncontradoException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void toggleReproduccion() {
        if (audioReproduciendo == null) return;
        reproduciendo = !reproduciendo;
        btnPlay.setText(reproduciendo ? "⏸" : "▶");
        if (reproduciendo && timerReproduccion != null) timerReproduccion.start();
        else if (timerReproduccion != null) timerReproduccion.stop();
    }

    // ============================================================
    //  CERRAR SESION (vuelve al login, NO cierra app)
    // ============================================================
    private void cerrarSesionYVolverAlLogin() {
        // Detener reproduccion
        reproduciendo = false;
        if (timerReproduccion != null) timerReproduccion.stop();
        audioReproduciendo = null;

        // Resetear UI
        lblInfoCancion.setText("Selecciona una cancion");
        lblInfoArtista.setText("MusicGO - Galeria Noir");
        btnPlay.setText("▶");
        progressBar.setValue(0);

        // Limpiar usuario y mostrar login
        usuarioActual = null;
        lblUsuarioSidebar.setText("Sin sesion");
        limpiarPanelCentral();
        mostrarPantallaLogin();
    }

    // ============================================================
    //  DIALOGOS
    // ============================================================
    private void mostrarDialogoCrearPlaylist() {
        if (usuarioActual == null) {
            JOptionPane.showMessageDialog(this, "Inicia sesion primero", "MusicGO", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String nombre = JOptionPane.showInputDialog(this, "Nombre de la playlist:", "Nueva Playlist", JOptionPane.PLAIN_MESSAGE);
        if (nombre != null && !nombre.trim().isEmpty()) {
            try {
                gestorPlaylists.crearPlaylist(usuarioActual.getId(), nombre.trim());
                JOptionPane.showMessageDialog(this, "Playlist creada exitosamente", "MusicGO", JOptionPane.INFORMATION_MESSAGE);
                mostrarPlaylists();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void mostrarDialogoAgregarCancionAPlaylist(Playlist p) {
        if (usuarioActual == null) return;
        List<Audio> audios = gestorCatalogo.getTodosLosAudios();
        if (audios == null || audios.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay canciones en el catalogo", "MusicGO", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String[] opciones = audios.stream()
                .filter(a -> a instanceof Cancion)
                .map(a -> a.getTitulo() + " - " + ((Cancion)a).getArtista())
                .toArray(String[]::new);

        if (opciones.length == 0) {
            JOptionPane.showMessageDialog(this, "No hay canciones disponibles", "MusicGO", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String seleccion = (String) JOptionPane.showInputDialog(this,
                "Selecciona una cancion:", "Agregar a playlist",
                JOptionPane.PLAIN_MESSAGE, null, opciones, opciones[0]);

        if (seleccion != null) {
            for (Audio a : audios) {
                if (a instanceof Cancion && seleccion.equals(a.getTitulo() + " - " + ((Cancion)a).getArtista())) {
                    try {
                        gestorPlaylists.agregarAudioAPlaylist(usuarioActual.getId(), p.getId(), a.getId());
                        JOptionPane.showMessageDialog(this, "Cancion agregada", "MusicGO", JOptionPane.INFORMATION_MESSAGE);
                        mostrarContenidoPlaylist(p);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                    break;
                }
            }
        }
    }

    // ============================================================
    //  COMPONENTES REUTILIZABLES
    // ============================================================

    private JLabel crearTituloSeccion(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(FONT_SUBTITLE);
        lbl.setForeground(NOIR_BEIGE);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private JPanel crearTarjetaPlaceholder(String titulo, String subtitulo, String extra) {
        JPanel tarjeta = new JPanel(new BorderLayout(0, 8));
        tarjeta.setBackground(NOIR_DARK);
        tarjeta.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        tarjeta.setMaximumSize(new Dimension(200, 260));

        JLabel lblCover = new JLabel("🎵");
        lblCover.setFont(new Font("Segoe UI", Font.PLAIN, 48));
        lblCover.setHorizontalAlignment(SwingConstants.CENTER);
        lblCover.setBackground(NOIR_GRAY);
        lblCover.setOpaque(true);
        lblCover.setPreferredSize(new Dimension(0, 160));
        lblCover.setBorder(BorderFactory.createLineBorder(NOIR_BLACK, 1));
        tarjeta.add(lblCover, BorderLayout.CENTER);

        JPanel info = new JPanel(new GridLayout(3, 1, 0, 2));
        info.setBackground(NOIR_DARK);
        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(FONT_BODY);
        lblTitulo.setForeground(NOIR_BEIGE);
        info.add(lblTitulo);
        JLabel lblSub = new JLabel(subtitulo);
        lblSub.setFont(FONT_SMALL);
        lblSub.setForeground(NOIR_BROWN);
        info.add(lblSub);
        JLabel lblExtra = new JLabel(extra);
        lblExtra.setFont(FONT_SMALL);
        lblExtra.setForeground(NOIR_GRAY);
        info.add(lblExtra);
        tarjeta.add(info, BorderLayout.SOUTH);

        return tarjeta;
    }

    private JPanel crearTarjetaAudio(String titulo, String subtitulo, String extra, Audio audio) {
        JPanel tarjeta = new JPanel(new BorderLayout(0, 8));
        tarjeta.setBackground(NOIR_DARK);
        tarjeta.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        tarjeta.setCursor(new Cursor(Cursor.HAND_CURSOR));
        tarjeta.setMaximumSize(new Dimension(200, 260));

        JLabel lblCover = new JLabel("🎵");
        lblCover.setFont(new Font("Segoe UI", Font.PLAIN, 48));
        lblCover.setHorizontalAlignment(SwingConstants.CENTER);
        lblCover.setBackground(NOIR_GRAY);
        lblCover.setOpaque(true);
        lblCover.setPreferredSize(new Dimension(0, 160));
        lblCover.setBorder(BorderFactory.createLineBorder(NOIR_BLACK, 1));
        tarjeta.add(lblCover, BorderLayout.CENTER);

        JPanel info = new JPanel(new GridLayout(3, 1, 0, 2));
        info.setBackground(NOIR_DARK);
        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(FONT_BODY);
        lblTitulo.setForeground(NOIR_BEIGE);
        info.add(lblTitulo);
        JLabel lblSub = new JLabel(subtitulo);
        lblSub.setFont(FONT_SMALL);
        lblSub.setForeground(NOIR_BROWN);
        info.add(lblSub);
        JLabel lblExtra = new JLabel(extra);
        lblExtra.setFont(FONT_SMALL);
        lblExtra.setForeground(NOIR_GRAY);
        info.add(lblExtra);
        tarjeta.add(info, BorderLayout.SOUTH);

        tarjeta.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { tarjeta.setBackground(new Color(50,50,50)); info.setBackground(new Color(50,50,50)); }
            @Override public void mouseExited(MouseEvent e) { tarjeta.setBackground(NOIR_DARK); info.setBackground(NOIR_DARK); }
            @Override public void mouseClicked(MouseEvent e) {
                if (audio instanceof Cancion) reproducirCancion((Cancion)audio);
                else if (audio instanceof EpisodioPodcast) reproducirPodcast((EpisodioPodcast)audio);
            }
        });

        return tarjeta;
    }

    private JPanel crearTarjetaProducto(Producto p) {
        JPanel tarjeta = new JPanel(new BorderLayout(0, 8));
        tarjeta.setBackground(NOIR_DARK);
        tarjeta.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        tarjeta.setMaximumSize(new Dimension(300, 320));

        JLabel lblCover = new JLabel("🛍️");
        lblCover.setFont(new Font("Segoe UI", Font.PLAIN, 48));
        lblCover.setHorizontalAlignment(SwingConstants.CENTER);
        lblCover.setBackground(NOIR_GRAY);
        lblCover.setOpaque(true);
        lblCover.setPreferredSize(new Dimension(0, 160));
        lblCover.setBorder(BorderFactory.createLineBorder(NOIR_BLACK, 1));
        tarjeta.add(lblCover, BorderLayout.CENTER);

        JPanel info = new JPanel(new GridLayout(3, 1, 0, 4));
        info.setBackground(NOIR_DARK);
        JLabel lblNombre = new JLabel(p.getNombre());
        lblNombre.setFont(FONT_BODY);
        lblNombre.setForeground(NOIR_BEIGE);
        info.add(lblNombre);
        JLabel lblDesc = new JLabel("<html><body style='width:200px'>" + p.getDescripcion() + "</body></html>");
        lblDesc.setFont(FONT_SMALL);
        lblDesc.setForeground(NOIR_BROWN);
        info.add(lblDesc);

        JPanel panelPrecio = new JPanel(new BorderLayout());
        panelPrecio.setBackground(NOIR_DARK);
        JLabel lblPrecio = new JLabel("$" + p.getPrecio());
        lblPrecio.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblPrecio.setForeground(NOIR_BEIGE_MED);
        panelPrecio.add(lblPrecio, BorderLayout.WEST);
        JButton btnComprar = crearBotonNoir("Comprar");
        btnComprar.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnComprar.addActionListener(e -> comprarProducto(p));
        panelPrecio.add(btnComprar, BorderLayout.EAST);
        info.add(panelPrecio);

        tarjeta.add(info, BorderLayout.SOUTH);
        return tarjeta;
    }

    private JPanel crearTarjetaEstadistica(String titulo, String valor, Color color) {
        JPanel tarjeta = new JPanel(new BorderLayout());
        tarjeta.setBackground(NOIR_DARK);
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 4, 0, 0, color),
                BorderFactory.createEmptyBorder(24, 24, 24, 24)
        ));
        tarjeta.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(FONT_BODY);
        lblTitulo.setForeground(NOIR_BROWN);
        tarjeta.add(lblTitulo, BorderLayout.NORTH);

        JLabel lblValor = new JLabel(valor);
        lblValor.setFont(new Font("Georgia", Font.BOLD, 36));
        lblValor.setForeground(NOIR_BEIGE);
        tarjeta.add(lblValor, BorderLayout.CENTER);

        return tarjeta;
    }

    private JPanel crearMensajeVacio(String mensaje) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(NOIR_BLACK);
        JLabel lbl = new JLabel(mensaje);
        lbl.setFont(FONT_BODY);
        lbl.setForeground(NOIR_GRAY);
        panel.add(lbl);
        return panel;
    }

    private JPanel crearMensajeVacioConSub(String titulo, String subtitulo) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(NOIR_BLACK);

        JPanel inner = new JPanel();
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));
        inner.setBackground(NOIR_BLACK);

        JLabel lblIcon = new JLabel("🎵");
        lblIcon.setFont(new Font("Segoe UI", Font.PLAIN, 64));
        lblIcon.setAlignmentX(Component.CENTER_ALIGNMENT);
        inner.add(lblIcon);
        inner.add(Box.createVerticalStrut(16));

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(FONT_SUBTITLE);
        lblTitulo.setForeground(NOIR_BEIGE);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        inner.add(lblTitulo);

        if (!subtitulo.isEmpty()) {
            inner.add(Box.createVerticalStrut(8));
            JLabel lblSub = new JLabel(subtitulo);
            lblSub.setFont(FONT_BODY);
            lblSub.setForeground(NOIR_BROWN);
            lblSub.setAlignmentX(Component.CENTER_ALIGNMENT);
            inner.add(lblSub);
        }

        panel.add(inner);
        return panel;
    }

    private JButton crearBotonTab(String texto, boolean activo) {
        JButton btn = new JButton(texto);
        btn.setFont(FONT_SMALL);
        btn.setForeground(activo ? NOIR_BLACK : NOIR_BEIGE_MED);
        btn.setBackground(activo ? NOIR_BEIGE : NOIR_DARK);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(activo ? NOIR_BEIGE : NOIR_GRAY, 1, true),
                BorderFactory.createEmptyBorder(8, 20, 8, 20)
        ));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setSelected(activo);
        return btn;
    }

    private JButton crearBotonNoir(String texto) {
        JButton btn = new JButton(texto) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(NOIR_BROWN);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 24, 24);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(FONT_BUTTON);
        btn.setForeground(NOIR_BLACK);
        btn.setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 24));
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JButton crearBotonIcono(String texto, int tamano) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, tamano));
        btn.setForeground(NOIR_BEIGE_MED);
        btn.setBackground(NOIR_BLACK);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btn.setForeground(NOIR_BEIGE); }
            @Override public void mouseExited(MouseEvent e) { btn.setForeground(NOIR_BEIGE_MED); }
        });
        return btn;
    }

    private JButton crearBotonControl(String texto, int tamano) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, tamano));
        btn.setForeground(NOIR_BEIGE);
        btn.setBackground(NOIR_DARK);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btn.setForeground(NOIR_CREAM); }
            @Override public void mouseExited(MouseEvent e) { btn.setForeground(NOIR_BEIGE); }
        });
        return btn;
    }

    private JButton crearBotonTexto(String texto, Color color) {
        JButton btn = new JButton(texto);
        btn.setFont(FONT_SMALL);
        btn.setForeground(color);
        btn.setBackground(NOIR_BLACK);
        btn.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JPanel envolverEnScroll(JPanel panel) {
        JScrollPane scroll = new JScrollPane(panel);
        scroll.setBorder(null);
        scroll.setBackground(NOIR_BLACK);
        scroll.getViewport().setBackground(NOIR_BLACK);
        scroll.getVerticalScrollBar().setUI(new NoirScrollBarUI());
        scroll.getVerticalScrollBar().setBackground(NOIR_BLACK);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(NOIR_BLACK);
        wrapper.add(scroll, BorderLayout.CENTER);
        return wrapper;
    }

    // ============================================================
    //  FUNCIONALIDAD: COMPRAR PRODUCTO
    // ============================================================
    private void comprarProducto(Producto p) {
        if (usuarioActual == null) {
            JOptionPane.showMessageDialog(this, "Inicia sesion para comprar", "MusicGO", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Deseas comprar '" + p.getNombre() + "' por $" + p.getPrecio() + "?",
                "Confirmar compra", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            boolean exito = gestorCompras.comprarProducto(usuarioActual.getId(), p.getId());
            if (exito) {
                JOptionPane.showMessageDialog(this, "Compra exitosa: " + p.getNombre(), "MusicGO", JOptionPane.INFORMATION_MESSAGE);
                usuarioActual.refrescarConteoBiblioteca();
            } else {
                JOptionPane.showMessageDialog(this, "Transaccion rechazada", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // ============================================================
    //  SCROLLBAR PERSONALIZADA
    // ============================================================
    private static class NoirScrollBarUI extends BasicScrollBarUI {
        @Override protected void configureScrollBarColors() {
            this.thumbColor = NOIR_GRAY;
            this.trackColor = NOIR_BLACK;
        }
        @Override protected JButton createDecreaseButton(int o) { return createZeroButton(); }
        @Override protected JButton createIncreaseButton(int o) { return createZeroButton(); }
        private JButton createZeroButton() {
            JButton b = new JButton(); b.setPreferredSize(new Dimension(0, 0)); return b;
        }
        @Override protected void paintThumb(Graphics g, JComponent c, Rectangle r) {
            if (r.isEmpty()) return;
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(thumbColor);
            g2.fillRoundRect(r.x + 2, r.y, r.width - 4, r.height, 6, 6);
            g2.dispose();
        }
    }

    // ============================================================
    //  MAIN (para pruebas directas)
    // ============================================================
    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        catch (Exception e) { e.printStackTrace(); }

        SwingUtilities.invokeLater(() -> {
            RepositorioDatos repo = new RepositorioDatos();
            GestorUsuarios gu = new GestorUsuarios(repo);
            GestorCatalogo gc = new GestorCatalogo();
            gc.cargarDesdeJson();
            GestorPlaylists gp = new GestorPlaylists(gu, gc);
            GestorReproduccion gr = new GestorReproduccion(gu, gc);
            GestorCompras gcom = new GestorCompras(gc, gu, repo);
            GestorEstadisticas ge = new GestorEstadisticas(gu);

            MusicGOApp app = new MusicGOApp(gu, gc, gp, gr, gcom, ge);
            app.setVisible(true);
        });
    }
}
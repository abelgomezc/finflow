import { motion } from 'framer-motion'

/**
 * Fondo animado con tema financiero para paginas de login/registro.
 * Incluye particulas flotantes, iconos de dinero y gradientes animados.
 */
export default function AnimatedBackground() {
  // Generar particulas aleatorias
  const particles = Array.from({ length: 15 }, (_, i) => ({
    id: i,
    size: Math.random() * 30 + 15,
    x: Math.random() * 100,
    delay: Math.random() * 3,
    duration: Math.random() * 8 + 12,
  }))

  // Lineas de flujo
  const flowLines = Array.from({ length: 5 }, (_, i) => ({
    id: i,
    top: 15 + i * 18,
    delay: i * 0.8,
    duration: 3 + i * 0.5,
  }))

  return (
    <div className="absolute inset-0 overflow-hidden" style={{ zIndex: 0 }}>
      {/* Fondo gradiente animado */}
      <motion.div
        className="absolute inset-0"
        style={{
          background: 'linear-gradient(135deg, #0d9488 0%, #0f766e 25%, #134e4a 50%, #0f766e 75%, #0d9488 100%)',
          backgroundSize: '400% 400%',
        }}
        animate={{
          backgroundPosition: ['0% 50%', '100% 50%', '0% 50%'],
        }}
        transition={{
          duration: 15,
          repeat: Infinity,
          ease: 'easeInOut',
        }}
      />

      {/* Capa de overlay oscuro */}
      <div className="absolute inset-0 bg-black/20" />

      {/* Grid de datos */}
      <div
        className="absolute inset-0"
        style={{
          backgroundImage: `
            linear-gradient(rgba(255,255,255,0.05) 1px, transparent 1px),
            linear-gradient(90deg, rgba(255,255,255,0.05) 1px, transparent 1px)
          `,
          backgroundSize: '60px 60px',
        }}
      />

      {/* Lineas de flujo horizontal (simulando transferencias) */}
      {flowLines.map((line) => (
        <motion.div
          key={line.id}
          className="absolute h-[2px] w-32"
          style={{
            top: `${line.top}%`,
            left: '-150px',
            background: 'linear-gradient(90deg, transparent, rgba(255,255,255,0.6), transparent)',
          }}
          animate={{
            x: ['0vw', '120vw'],
          }}
          transition={{
            duration: line.duration,
            delay: line.delay,
            repeat: Infinity,
            ease: 'linear',
          }}
        />
      ))}

      {/* Particulas flotantes (monedas/billetes simbolicos) */}
      {particles.map((particle) => (
        <motion.div
          key={particle.id}
          className="absolute rounded-full"
          style={{
            width: particle.size,
            height: particle.size,
            left: `${particle.x}%`,
            background: 'radial-gradient(circle at 30% 30%, rgba(255,255,255,0.3), rgba(255,255,255,0.05))',
            border: '1px solid rgba(255,255,255,0.2)',
          }}
          initial={{ y: '110vh', opacity: 0.3 }}
          animate={{
            y: '-10vh',
            opacity: [0.3, 0.6, 0.3],
          }}
          transition={{
            duration: particle.duration,
            delay: particle.delay,
            repeat: Infinity,
            ease: 'linear',
          }}
        />
      ))}

      {/* Simbolo de dolar flotante central */}
      <motion.div
        className="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 text-white/5 text-[300px] font-bold select-none pointer-events-none"
        animate={{
          scale: [1, 1.05, 1],
          rotate: [0, 5, -5, 0],
        }}
        transition={{
          duration: 10,
          repeat: Infinity,
          ease: 'easeInOut',
        }}
      >
        $
      </motion.div>

      {/* Circulos decorativos */}
      <motion.div
        className="absolute -top-20 -right-20 w-96 h-96 rounded-full border-2 border-white/10"
        animate={{
          scale: [1, 1.1, 1],
          rotate: 360,
        }}
        transition={{
          duration: 25,
          repeat: Infinity,
          ease: 'linear',
        }}
      />
      <motion.div
        className="absolute -bottom-32 -left-32 w-80 h-80 rounded-full border-2 border-white/10"
        animate={{
          scale: [1.1, 1, 1.1],
          rotate: -360,
        }}
        transition={{
          duration: 20,
          repeat: Infinity,
          ease: 'linear',
        }}
      />

      {/* Ondas en la parte inferior */}
      <div className="absolute bottom-0 left-0 right-0 h-40 overflow-hidden">
        <motion.div
          className="absolute bottom-0 left-0 w-[200%] h-full"
          style={{
            background: 'linear-gradient(90deg, transparent, rgba(255,255,255,0.1) 25%, rgba(255,255,255,0.2) 50%, rgba(255,255,255,0.1) 75%, transparent)',
          }}
          animate={{
            x: ['-50%', '0%'],
          }}
          transition={{
            duration: 6,
            repeat: Infinity,
            ease: 'linear',
          }}
        />
      </div>
    </div>
  )
}

(set-logic QF_NIA)
(declare-fun f_0 () Int)
(declare-fun f_2 () Int)
(declare-fun g_1 () Int)
(declare-fun f_1 () Int)
(declare-fun g_0 () Int)
(declare-fun g_2 () Int)

(assert (>= (+ 0 (* 1 f_0)) (+ 0 (* 1 g_0))))
(assert (>= (+ 0 (* 1 f_1)) (+ (+ 0 (* 1 g_1)) (* 1 g_2))))
(assert (>= (+ 0 (* 1 f_2)) 0))
(assert (or (> (+ 0 (* 1 f_0)) (+ 0 (* 1 g_0))) (> (+ 0 (* 1 f_1)) (+ (+ 0 (* 1 g_1)) (* 1 g_2))) (> (+ 0 (* 1 f_2)) 0)))
(assert (and (>= f_0 0) (>= f_2 1) (>= g_1 1) (>= f_1 1) (>= g_0 0) (>= g_2 1)))
(assert (and (or (> f_0 0) (> f_2 1) (> f_1 1)) (or (> g_1 1) (> g_0 0) (> g_2 1))))

(check-sat)
(get-model)


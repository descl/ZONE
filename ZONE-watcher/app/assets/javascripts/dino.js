//contribution from @yellowiscool https://twitter.com/yellowiscool

$(document).ready(function() {
(function() {
    "use strict"

    var dino = $('#dino-logo img');

    function creerOeil(topPos, leftPos) {

        var oeil = $('<div class="oeil">'),
            pupille = $('<div class="pupille">');
        oeil.append(pupille).appendTo(document.body);

        return function placerYeux(e, dinoPos) {
            // il y a des constantes car j'aime les lapins

            var dinoTop = dinoPos.top,
                dinoLeft = dinoPos.left,
                dinoWidth = dino.width(),

                ratio = dinoWidth/270.0,
                rayon = 13*ratio,

                top = dinoTop + topPos*ratio,
                left = dinoLeft + leftPos*ratio;

            oeil.offset({
                top: top - rayon,
                left: left - rayon
            });

            oeil.css({
                width: rayon*2,
                height: rayon*2
            });

            pupille.css({
                width: 9*ratio,
                height: 9*ratio
            });


            // L'algo vient de xeyes
            var mouseX = e.clientX,
                mouseY = e.clientY,
                dx = mouseX - left,
                dy = mouseY - top,
                cx = 0,
                cy = 0,
                oeilWidth = 22;

            if (dx == 0 && dy == 0) {
                cx = left;
                cy = top;
            } else {
                var angle = Math.atan2(dy, dx),
                    cosa = Math.cos(angle),
                    sina = Math.sin(angle),
                    h = Math.sqrt(oeilWidth * cosa * oeilWidth * cosa +
                        oeilWidth * sina * oeilWidth * sina),
                    x = (oeilWidth * oeilWidth) * cosa / h,
                    y = (oeilWidth * oeilWidth) * sina / h,
                    dist = 0.28*ratio * Math.sqrt(x * x + y * y);
                // 0.28 règle la distance minimale entre la pupille et le bord de l'oeil,
                // plus c'est grand plus la pupille peut aller (très) loin

                if (dist > Math.sqrt(dx * dx + dy * dy)) {
                    cx = dx + left;
                    cy = dy + top;
                } else {
                    cx = dist * cosa + left;
                    cy = dist * sina + top;
                }
            }

            var marginPupille = 4.5*ratio;

            pupille.offset({
                left: cx-marginPupille,
                top: cy-marginPupille
            });
        };
    }

    var placerOeil1 = creerOeil(23, 66.5),
        placerOeil2 = creerOeil(23, 149.5);

    $(window).on('resize mousemove touchmove', function(e) {
        var pos = dino.position();
        placerOeil1(e, pos);
        placerOeil2(e, pos);
    });
})();
});
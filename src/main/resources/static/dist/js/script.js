jQuery(function ($) {
    var util = {
      jsonAjax: function (url,data,success,error) {
          $.ajax({
              headers: {
                  'Accept': 'application/json',
                  'Content-Type': 'application/json'
              },
              type: 'post',
              url: url,
              dataType: 'json',
              data: data,
              cache: false,
              success: success,
              error: error
          });
      }
    };
    //init data tables
    $('.data-table').DataTable({
        'paging': true,
        'lengthChange': false,
        'searching': false,
        'ordering': true,
        'info': true,
        'autoWidth': false
    });

    //init date picker
    $('#datepicker').datepicker({
        autoclose: true
    });

    $('.sidebar-menu').tree();


    if (localStorage.getItem("successProp") == 1) {
        $('.alert-row').append(' <div class="alert alert-success alert-dismissible">\n' +
            '                            <button type="button" class="close" data-dismiss="alert" aria-hidden="true">×</button>\n' +
            '                            <h4><i class="icon fa fa-check"></i> Ajout effectue</h4>\n' +
            '                            vous trouverez les fichiers dans <a href="/mes-proposition/envoye"> preposition envoye</a>\n' +
            '                        </div>');
        localStorage.removeItem("successProp");
        setTimeout(function () {
            $('.alert-row').children().remove();
        }, 4000);
    }
    $('#datepicker').datepicker({
        autoclose: true
    });
    //Initialize Select2 Elements
    $('.select2').select2();
    var addRowBehaviour = function (e) {
        e.preventDefault();
        var $tableBody = $(this).parent().parent().parent();
        var markup = '<tr class="objet-row">\n' +
            '                      <td><input class="form-control" value="" ></td>\n' +
            '                      <td><input class="form-control" value=""></td>\n' +
            '                      <td>\n' +
            '                        <input class="form-control" value="">\n' +
            '                      </td>\n' +
            '                      <td class="text-center"><button class="badge bg-red btn remove-row-objet">x</button></td>\n' +
            '                    </tr>';
        $tableBody.append(markup);
        $('.remove-row-objet').unbind('click');
        $('.remove-row-objet').on('click', function (e) {
            e.preventDefault();
            $(this).parent().parent().remove();
        });
    };
    $('.add-row-objet').on('click', addRowBehaviour);
    $('#prop-form').on('submit', function (e) {
        console.log("Im in");
        e.preventDefault();
        var propositions = [];
        var destinataire = [];
        $('.select2-selection__choice').each(function () {
            destinataire.push($(this).attr('title').trim());
        });
        $('.prop-row').each(function () {
            var objets = [];
            var typePropo = $(this).find('.type-prop').val();

            $(this).find('.objet-row').each(function () {
                var nom = $(this).find('td:eq(0) input').val();
                var type = $(this).find('td:eq(1) input').val();
                var valeur = $(this).find('td:eq(2) input').val();
                if (nom.trim().length && type.trim().length && valeur.trim().length)
                    objets.push({
                        nom: nom,
                        type: type,
                        valeur: valeur
                    });
            });
            if (typePropo.trim().length && objets.length)
                propositions.push({
                    typePropo: typePropo,
                    objets: objets
                })
        });

        if (destinataire.length && propositions.length) {
            var data;
            data = JSON.stringify({
                destinataires: destinataire,
                props: propositions
            });
            console.log(data);
            $.ajax({
                headers: {
                    'Accept': 'application/json',
                    'Content-Type': 'application/json'
                },
                type: 'post',
                url: $(this).attr('action'),
                dataType: 'json',
                data: data,
                cache: false,
                success: function (resp) {
                    if (resp.length && resp[0] == 'ok') {
                        localStorage.setItem("successProp", 1);
                        location.reload();
                    }
                },
                error: function () {
                    $.gritter.add({
                        title: 'Erreur',
                        text: 'Une erreur est survenu veuillez ressayer plustard',
                        class_name: 'gritter-green bg-green gritter-right'
                    });
                }
            });
        }

    });
    var $proposition = $('.prop-row').clone();
    var supprimerRowProp = function () {
        $(this).parent().parent().parent().parent().parent().remove();

    };
    $('.sup-prop').on('click', supprimerRowProp);
    $('#ajout-prop').on('click', function (e) {
        e.preventDefault();
        var $propositionClone = $proposition.clone();
        $propositionClone.find('.sup-prop').on('click', supprimerRowProp);
        $propositionClone.find('.add-row-objet').on('click', addRowBehaviour);
        $propositionClone.find('.remove-row-objet').on('click', function (e) {
            e.preventDefault();
            $(this).parent().parent().remove();
        });
        $('.prop-body').append($propositionClone);
    });


    $('.btn-action').on('click', function (e) {
        e.preventDefault();
        $.ajax({
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            type: 'post',
            url: '/demande/' + $(this).data('action'),
            data: '["' + $(this).data('dmd-id') + '"]',
            dataType: 'json',
            cache: false,
            success: function (data) {
                console.log(data);
                if (data.length && data[0] === 'ok') {
                    alert('demande confirmée');
                    location.reload();
                }


            }
        });
    });
    $('.btn-prop-action').on('click', function (e) {
        e.preventDefault();
        $.ajax({
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            type: 'post',
            url: '/prop/' + $(this).data('action'),
            data: '["' + $(this).data('fic') + '"]',
            dataType: 'json',
            cache: false,
            success: function (data) {
                console.log(data);
                if (data.length && data[0] === 'ok') {
                    alert('demande confirmée');
                    location.reload();
                }


            }
        });
    });

    $('#contre-prop-form').on('submit', function (e) {
        e.preventDefault();
        var contreProps = [];

        $('.proposition-row').each(function () {

            var objets = [];
            var typePropo = $(this).find('.type-prop').val();
            $(this).find('.objet-row').each(function () {
                var nom = $(this).find('td:eq(0) input').val();
                var type = $(this).find('td:eq(1) input').val();
                var valeur = $(this).find('td:eq(2) input').val();
                if (nom.trim().length && type.trim().length && valeur.trim().length)
                    objets.push({
                        nom: nom,
                        type: type,
                        valeur: valeur
                    });
            });
            contreProps.push({
                contrePropTitre: $(this).find('.prop-titre').val(),
                typePropo: typePropo,
                contrePropSrc: $(this).data('prop'),
                contrePropObjets: objets,
                msg:$(this).data('msg')
            });
        });
        var cp = JSON.stringify(contreProps);
        console.log(cp);
        util.jsonAjax('/contre-proposition',cp,function (data) {
            alert('Contre proposition envoye');
            location.reload();
        });

        console.log(contreProps);
    });


});

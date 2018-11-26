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
    var $dataTables =$('.data-table');
    if($dataTables.length) {
        $dataTables.DataTable({
            'paging': true,
            'lengthChange': false,
            'searching': false,
            'ordering': true,
            'info': true,
            'autoWidth': false
        });
    }
    //init date picker
    var $datePicker = $('#datepicker')
    if($datePicker.length) {
        $datePicker.datepicker({
            autoclose: true
        });
        $datePicker.datepicker({
            autoclose: true
        });
    }

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
        console.log("hey Im in");
        e.preventDefault();
        var propositions = [];
        var destinataire = [];
        $('.select2-selection__choice').each(function () {
            destinataire.push($(this).attr('title').trim());
        });
        $('.prop-row').each(function () {
            var objets = [];
            var typePropo = $(this).find('.type-prop').val();
            var titrePropo = $(this).find('.titre-prop').val();
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
                    titrePropo:titrePropo,
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
            util.jsonAjax($(this).attr('action'),data,function (resp) {
                if (resp.length && resp[0] == 'ok') {
                    localStorage.setItem("successProp", 1);
                    location.reload();
                }
            },function () {
                $.gritter.add({
                    title: 'Erreur',
                    text: 'Une erreur est survenu veuillez ressayer plustard',
                    class_name: 'gritter-green bg-green gritter-right'
                });
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
        util.jsonAjax('/demande/' + $(this).data('action'),'["' + $(this).data('dmd-id') + '"]',function (data) {
            console.log(data);
            if (data.length && data[0] === 'ok') {
                alert('demande confirmée');
                location.reload();
            }

        });
    });
    var openMsg = false;
    var openContrProp = false;
     propsTypes = [];

    $('.rep-msg').on('click',function (e) {
        var propId =$(this).parent().parent().parent().data('prop');
        e.preventDefault();
        openMsg = !openMsg;
        if(openContrProp) {
            openContrProp = false;
            $('#contre-prop-box-' + propId).slideUp();
        }
        $($(this).data('target')).slideToggle();
        propsTypes[propId]='msg';

    });

    $('.rep-contre-prop').on('click',function (e) {
        e.preventDefault();
        var propId = $(this).parent().parent().parent().data('prop');
        openContrProp = !openContrProp;
        if(openMsg){
            openMsg=false;
            $('#rep-msg-box-' + propId).slideUp();
        }
        $($(this).data('target')).slideToggle();
        propsTypes[propId]='contre-prop';
    });

    $('#contre-prop-form').on('submit', function (e) {
        e.preventDefault();
        var contreProps = [];

        $('.proposition-row').each(function () {
            var propId= $(this).data('prop');
            if(propsTypes[propId] === 'contre-prop') {
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
                    contrePropSrc: propId,
                    contrePropObjets: objets,
                    msg: $(this).data('msg'),
                    propsType:'contre-prop'
                });
            }else if(propsTypes[propId] === 'msg'){
                contreProps.push({
                    msgTxt: $(this).find('.prop-msg').val(),
                    propId: propId,
                    msg: $(this).data('msg'),
                    propsType:'msg'
                })
            }
        });
        var cp = JSON.stringify(contreProps);
        console.log(cp);
        util.jsonAjax('/repondre-propositions',cp,function (data) {
            console.log(data);
            alert('Contre proposition envoye');
            location.href='/mes-proposition/recus';
        });

        console.log(contreProps);
    });

    $('.close-modal').on('click',function (e) {
        e.preventDefault();
        $($(this).data('dismiss')).modal('hide');
    });
    var $cred = $('#cred');
    if($cred.length) {
        var passed = false;
        var $clonedCred = $cred.clone();
        $cred.remove();
        $('#destinataire-dmd').on('blur', function () {
            if ($(this).val().trim().length) {
                console.log($(this).val());
                util.jsonAjax('/check-utilisateur', $(this).val(), function (data) {
                    if (data.length && data[0] == '0') {
                        $('#cred-container').append($clonedCred);
                        $('#cred-container').slideDown();
                        passed=true;
                    }else if(data.length && data[0] == '-1'){
                        alert('Vous etes deja amis avec cet utilisateur');
                        location.reload();
                    }
                })
            }else{
                if($('#cred-container').children().length)
                    $('#cred-container').children().remove()
            }
        })
    }


});

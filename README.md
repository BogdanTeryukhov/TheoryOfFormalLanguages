# Исходные данные
В файле input.txt содержатся исходные данные (грамматика, изначальное слово и редактированное слово для инкреметального разбора)<br>
Пример входных данных:<br>

<div>
<p>Grammar:</p>  
S->adcBAFk<br>  
A->f|c<br>
A->eps<br>  
B->aCd|eps<br>  
C->k<br>
C->h<br>
C->eps<br>
F->p|h<br>

<p>Word:</p>
adcfhk<br>

<p>Redacted Word For Incremental Parsing:</p>
adcakdhk<br>
</div>
  
*слова могут быть записаны на разных строках, но по итогу сконкатенируются в одно<br>
**правила грамматики можно писать как в одну строку через |, так и на разных строках<br>
***правила пишутся слитно, без пробелов<br>

# Запуск программы
1. Устанавливаете в program arguments путь к файлу input.txt
2. Запускаете файл Application.java
3. Далее в консоль выводятся деревья разбора (либо ошибка, что грамматика не LL(1) или слово(-а) нельзя разобрать)

import re

with open("ViM.txt", "r", encoding='utf-8') as file:
    contents = file.readlines()
    full_str = ' '.join(contents)
chars = 'абвгдеёжзийклмнопрстуфхцчшщъыьэюяАБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ'
for char in chars:
    count = full_str.count(char)
    print(char, count)
print("Сдвиг: ")
n = int(input())
with open("ViM_glava.txt", "r", encoding='windows-1251') as file:
    contents_gl = file.readlines()
    part = ' '.join(contents_gl)
res = ''
reg = re.compile('[^а-яА-Я ]')
part = reg.sub('', part)
#part = part.replace(" ", "")
for c in part:
    if c == " ":
        res += " "
    else:
        res += chars[(chars.index(c) + n) % len(chars)]
ViM_edit_crypt = open("vim_crypt.txt", "w")
ViM_edit_crypt.write(res)
ViM_edit_crypt.close()
with open("vim_crypt.txt", "r", encoding='windows-1251') as file:
    contents = file.readlines()
    full_str = ' '.join(contents)
for char in chars:
    count = full_str.count(char)
    print(char, count)
print("Сдвиг (по таблице): ")
n = int(input())

with open("ViM_crypt.txt", "r", encoding='windows-1251') as file:
    contents_gl = file.readlines()
    part = ' '.join(contents_gl)
res = ''
reg = re.compile(chars)
part = reg.sub('', part)
#part = part.replace(" ", "")
for c in part:
    if c == " ":
        res += " "
    else:
        res += chars[(chars.index(c) + n) % len(chars)]
ViM_edit_crypt = open("vim_decrypt.txt", "w")
ViM_edit_crypt.write(res)
ViM_edit_crypt.close()

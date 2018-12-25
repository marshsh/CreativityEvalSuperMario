import csv
import numpy as np
from sklearn.cluster import DBSCAN
import pickle
import shutil
import os

import matplotlib.pyplot as plt


def main():




	# Eval.loadEvaluations()


	# MakeClusters.parteOriginal()


	# uno = MakeClusters()
	# uno.setNiveles( [[[0,0,0],[9,16,0]],[[0,0,0],[9,5,16]]] )
	# uno.matrizDistancias()


	# a = [[0,1,2,3,4], [10,20,30,40], [100,200,300,400]]

	# Eval.loadEvaluations()
	# Eval.distanciaRI()
	# Eval.plotDist('DistanciasPickle')
	# Eval.imprimeCuantosMenorQue(15)

	# Eval.guardaValiosos('x')

	a = Eval()

	# Eval.criterio1()
	# Eval.criterio2()
	# Eval.criterio9()
	# Eval.criterio10a()
	# Eval.criterio11()
	# Eval.criterio13(15)
	# Eval.criterio15(15)
	# Eval.criterio3()
	# Eval.criterio4(5) # Por el momento recomendamos -5 para calidad
	# Eval.criterio12()
	# Eval.criterio14(5) # Por el momento recomendamos -5 para calidad
	# Eval.criterio16(5) # Por el momento recomendamos -5 para calidad
	# Eval.criterio17(10,5) # Por el momento recomendamos -5 para calidad
	# Eval.criterio18(100,5) # Por el momento recomendamos -5 para calidad

	Eval.guardaValiosos('')



def dropPickle(item, file_Name):
	with open(file_Name,'wb') as fileObject:
		pickle.dump(item,fileObject)   

def loadPickle(file_Name):
	with open(file_Name,'rb') as fileObject:
		b = pickle.load(fileObject)   
	return b






class Eval():
	paramPlagio = 5 #Incluyente. (Si cambiamos 5 losetas, sigue siendo plagio).

	valueDic = {} # Todos los niveles evaluados.
	incompletables = {}
	deCalidad = {}
	completable = {}
	originales = []
	generados = []
	generadosCompletables = []
	distancias = []
	typDic = {}	
	plagiados = {}



	def __init__(self):
		print('Inicia objeto Evaluation')

		numero = 4



		Eval.originales = MakeClusters.arregloNiveles('NivelOriginal/Partes', 231)
		Eval.generados = MakeClusters.arregloNiveles('zTextosNiveles4', 1000)
		# Eval.generadosCompletables = MakeClusters.arregloNiveles('zTextosNiveles4', 1000, filtrar = True)

		# Eval.distanciaRI()

		Eval.loadEvaluations()


		# fileName = 'zTextosNiveles' + str(numero)
		# Eval.typicalityDic(fileName, 1000)

		Eval.typDic = loadPickle('TypicPickle')
		Eval.plagiados = loadPickle('PlagiadosPickle')





	def criterio1():
		promedio = 0

		for key in Eval.typDic.keys():
			promedio = promedio + Eval.typDic[key]

		r = promedio / len(Eval.typDic.keys())


		print('\n \n ')
		print('suma de distancias   :  ', promedio )
		print('Número de Elementos   :  ', len(Eval.typDic.keys()) )
		print('criterio1   :  ', r )


	def criterio2():
		numeroDeNormales = 0

		for key in Eval.typDic.keys():

			if ( Eval.typDic[key] >= 10 ) & ( Eval.typDic[key] != 400 ):
				numeroDeNormales += 1

		r = numeroDeNormales / len(Eval.typDic.keys())

		print('\n \n ')
		print('Cuenta de niveles en Ta   :  ', numeroDeNormales )
		print('Número de Elementos   :  ', len(Eval.typDic.keys()) )
		print('criterio2   :  ', r )


	def criterio9():

		numeroDePlagios = len(Eval.plagiados)

		r = numeroDePlagios / len(Eval.originales) 

		print('\n \n ')
		print('Parámetro de distancia para que un nivel se considere plagio  :  ', Eval.paramPlagio)
		print('Cuenta de niveles Originales que son Plagios por el sistema   :  ', numeroDePlagios )
		print('Número de Originales   :  ', len(Eval.originales) )
		print('criterio9   :  ', r )


	def criterio10a(): # Proporción de niveles generados que no son plagio de los originales.

		numeroDeNiveles = 0

		for key in Eval.typDic.keys():

			if ( Eval.typDic[key] > Eval.paramPlagio ):
				numeroDeNiveles += 1

		r = numeroDeNiveles / len(Eval.typDic.keys())

		print('\n \n ')
		print('Parámetro de distancia para que un nivel se considere plagio  :  ', Eval.paramPlagio)
		print('Cuenta de niveles que NO son un plagio   :  ', numeroDeNiveles )
		print('Número de Elementos   :  ', len(Eval.typDic.keys()) )
		print('criterio10a   :  ', r )



	def criterio11():

		numeroDeNiveles = 0
		suma = 0

		for key in Eval.typDic.keys():

			if ( Eval.typDic[key] > Eval.paramPlagio ):
				numeroDeNiveles += 1
				suma += Eval.typDic[key]

		r = suma / numeroDeNiveles

		print('\n \n ')
		print('Parámetro de distancia para que un nivel se considere plagio  :  ', Eval.paramPlagio)
		print('Suma de typ(a) de niveles a que nos son plagio   :  ', suma )
		print('Número de Elementos   :  ', numeroDeNiveles )
		print('criterio10a   :  ', r )



	def criterio13(alpha):

		numeroDeNiveles = len(Eval.generados)
		cuenta = 0

		for key in Eval.typDic.keys():

			if ( Eval.typDic[key] > alpha ) & ( Eval.typDic[key] != 400 ) :
				cuenta += 1
				

		r = cuenta / numeroDeNiveles

		print('\n \n ')
		print('Parámetro alpha  :  ', alpha)
		print('Número de niveles pasables más atípicos que ', alpha, '   :  ', cuenta )
		print('Número de Elementos   :  ', numeroDeNiveles ) 
		print('criterio13   :  ', r )



	def criterio15(alpha):

		numeroDeNiveles = 0
		cuenta = 0

		for key in Eval.typDic.keys():

			if ( Eval.typDic[key] > alpha ) & ( Eval.typDic[key] != 400 ) :
				cuenta += 1

			if ( Eval.typDic[key] > Eval.paramPlagio ) :
				numeroDeNiveles += 1


		r = cuenta / numeroDeNiveles

		print('\n \n ')
		print('Parámetro alpha  :  ', alpha)
		print('Número de niveles pasables más atípicos que ', alpha, '   :  ', cuenta )
		print('Número de niveles generados que nos son plagio  :  ', numeroDeNiveles ) 
		print('criterio13   :  ', r )


	def criterio3():

		numeroDeNiveles = len(Eval.valueDic)
		suma = 0

		for key in Eval.valueDic.keys():
			suma += Eval.typDic[key]

		r = suma / numeroDeNiveles

		print('\n \n ')
		print('Suma de typ(a) de niveles a que nos son plagio   :  ', suma )
		print('Número de Elementos   :  ', numeroDeNiveles )
		print('criterio3   :  ', r )


	def criterio4(alpha):

		numeroDeNiveles = len(Eval.valueDic)
		cuenta = 0

		for key in Eval.valueDic.keys():

			if ( -Eval.valueDic[key] > alpha ) :
				cuenta += 1
				

		r = cuenta / numeroDeNiveles

		print('\n \n ')
		print('Parámetro alpha  :  ', alpha)
		print('Número de niveles con valor mayor que ', alpha, '   :  ', cuenta )
		print('Número de Elementos   :  ', numeroDeNiveles ) 
		print('criterio4   :  ', r )





	def criterio12():

		numeroDeNiveles = 0
		suma = 0

		for key in Eval.valueDic.keys():
			
			if ( Eval.typDic[key] > Eval.paramPlagio):
				suma += Eval.valueDic[key]
				numeroDeNiveles += 1

		r = suma / numeroDeNiveles

		print('\n \n ')
		print('Suma de val(a) de niveles a que nos son plagio   :  ', suma )
		print('Número de Elementos no plagia  :  ', numeroDeNiveles )
		print('criterio12 (average)  :  ', r )



	def criterio14(alpha):

		numeroDeNiveles = len(Eval.valueDic)
		cuenta = 0

		for key in Eval.valueDic.keys():

			if ( -Eval.valueDic[key] > alpha ) & ( Eval.typDic[key] > Eval.paramPlagio ) :
				cuenta += 1
				

		r = cuenta / numeroDeNiveles

		print('\n \n ')
		print('Parámetro alpha  :  ', alpha)
		print('Número de niveles no plagia con valor mayor que ', alpha, '   :  ', cuenta )
		print('Número total de Elementos generados   :  ', numeroDeNiveles ) 
		print('criterio14  (ratio)  :  ', r )


	def criterio16(alpha):

		numeroDeNiveles = 0
		cuenta = 0

		for key in Eval.valueDic.keys():

			if ( Eval.typDic[key] > Eval.paramPlagio ) :
				numeroDeNiveles += 1

				if ( -Eval.valueDic[key] > alpha ) & ( Eval.typDic[key] > Eval.paramPlagio ) :
					cuenta += 1
				

		r = cuenta / numeroDeNiveles

		print('\n \n ')
		print('Parámetro alpha  :  ', alpha)
		print('Número de niveles no plagia con valor mayor que ', alpha, '   :  ', cuenta )
		print('Número de Elementos no plagia   :  ', numeroDeNiveles ) 
		print('criterio16  (ratio)  :  ', r )


	def criterio17(alpha, gamma):

		numeroDeNiveles = 0
		cuenta = 0

		for key in Eval.valueDic.keys():

			if ( Eval.typDic[key] > Eval.paramPlagio ) :
				numeroDeNiveles += 1

				if ( -Eval.valueDic[key] > gamma ) & ( Eval.typDic[key] > alpha ) :
					cuenta += 1
				

		r = cuenta / numeroDeNiveles

		print('\n \n ')
		print('Parámetro alpha (tipicalidad)  :  ', alpha)
		print('Parámetro gamma (calidad) :  ', gamma)
		print('Número de niveles no plagia cona tipicalidad mayor que ', alpha, ' y calidad mayor que ', gamma ,'   :  ', cuenta )
		print('Número de Elementos no plagia   :  ', numeroDeNiveles ) 
		print('criterio17  (ratio)  :  ', r )



	def criterio18(alpha, gamma):

		numeroDeNiveles = 0
		cuenta = 0

		for key in Eval.valueDic.keys():

			if ( Eval.typDic[key] > Eval.paramPlagio ) :
				numeroDeNiveles += 1

				if ( -Eval.valueDic[key] > gamma ) & ( Eval.typDic[key] < alpha ) :
					cuenta += 1
				

		r = cuenta / numeroDeNiveles

		print('\n \n ')
		print('Parámetro alpha (tipicalidad)  :  ', alpha)
		print('Parámetro gamma (calidad) :  ', gamma)
		print('Número de niveles no plagia cona tipicalidad menor que ', alpha, ' y calidad mayor que ', gamma ,'   :  ', cuenta )
		print('Número de Elementos no plagia   :  ', numeroDeNiveles ) 
		print('criterio17  (ratio)  :  ', r )








	def guardaValiosos(name):

		Eval.loadEvaluations()

		for i in Eval.deCalidad.keys():
			source = 'zNivelesAleatorios4' + str(name) + '/'  + str(i) + '.jpg'
			target = 'zCalidad4' + str(name) + '/'  + str(i) + '.jpg'

			if not os.path.isdir('zCalidad4' + str(name)):
				os.makedirs('zCalidad4' + str(name))

			try:
			    shutil.copy(source, target)
			    print('Copiado ')
			except IOError as e:
			    print("Unable to copy file. %s" % e)
			except:
			    print("Unexpected error:", sys.exc_info())




	def loadEvaluations():
		fileName = 'EvaluacionesDic4.csv'
		Eval.valueDic = {}
		Eval.incompletables = {}
		Eval.deCalidad = {}
		Eval.completable = {}

		print('\n', Eval.deCalidad)
		print('\n', Eval.valueDic)

		print('\n', '**************************** \n \n \n ')


		with open(fileName, 'r') as csvfile:
			reader = csv.reader(csvfile,delimiter=',')

			for row in reader:
				Eval.valueDic[row[0]] = float(row[1])
				print(row)
				print(Eval.valueDic[row[0]])

				if float(row[1]) > -1:
					Eval.incompletables[row[0]] = float(row[1])

				if float(row[1]) < -7:
					Eval.deCalidad[row[0]] = float(row[1])
					print(Eval.deCalidad[row[0]], '\n\n\n')

				if float(row[1]) <= -1:
					Eval.completable[row[0]] = float(row[1])

		print('De Calidad :   ', len(Eval.deCalidad))
		print('Incompletables :   ', len(Eval.incompletables))
		print('Todos :   ', len(Eval.valueDic))

		print('\n', Eval.deCalidad)
		# print('\n', Eval.valueDic)


	# def filtroComp():
	# 	filtro = []
	# 	for i in range(1,len(valueDic)+1):
	# 		if str(i) not in Eval.incompletables:
	# 			filtro.append(True)
	# 		else:
	# 			filtro.append(False)

	# 	return filtro








# Se necesita haber cargado los valores loadEvaluations()
	def typicalityDic( fileName, cantidadNiveles, filtrar=False):

		typDic = {}

		for i in range(1,cantidadNiveles+1):
			fileNameI = fileName + '/' + 'Nivel_'+ str(i) +'.csv'

			if float(Eval.valueDic[str(i)]) > -1 :
				Eval.typDic[str(i)] = 400
			else:
				nivelm = MakeClusters.load1(fileNameI)
				Eval.typDic[str(i)] = Eval.distanciaAOrigin(nivelm)

		dropPickle(Eval.typDic,'TypicPickle')
		dropPickle(Eval.plagiados,'PlagiadosPickle')
		# print(Eval.typDic)









	def distanciaAOrigin(nivelm):
		disNivel = 1000000000000000

		for i, nivelO in enumerate(Eval.originales):
			distanciaO = MakeClusters.distanciaNiveles(nivelm,nivelO)
			disNivel = min(disNivel,distanciaO)

			if distanciaO <= Eval.paramPlagio:
				if( i in Eval.plagiados):
					Eval.plagiados[i] += 1
				else:
					Eval.plagiados[i] = 1


		# print(disNivel)
		return disNivel


	def distanciaRI():

		# Eval.originales = MakeClusters.arregloNiveles('NivelOriginal/Partes', 231)
		# Eval.generados = MakeClusters.arregloNiveles('zTextosNiveles2', 1000)
		# Eval.generadosCompletables = MakeClusters.arregloNiveles('zTextosNiveles2', 1000, filtrar = True)

		# print('Eval.originales con :  ', len(Eval.originales) )
		# print('Eval.generados con :  ', len(Eval.generadosCompletables) )


		Eval.distancias = []
		Eval.valorTyp = []


		for nivelG in Eval.generadosCompletables:

			disNivel = Eval.distanciaAOrigin(nivelG)

			Eval.distancias.append(disNivel)


		dropPickle(Eval.distancias,'DistanciasPickle')

		print(Eval.distancias)




	def plotDist(namePickle):
		distancias = loadPickle(namePickle)


		plt.hist(distancias, 50, density=1, facecolor='g',cumulative=True, alpha=0.75)
		plt.xlabel('Distancias')
		plt.ylabel('Cantidad de Niveles')
		plt.title('Histograma de Distancia a Niveles de Inspiración')
		# plt.text(60, .025, r'$\mu=100,\ \sigma=15$')
		# plt.axis([40, 160, 0, 0.03])
		plt.grid(True)
		plt.show()


	def cuantosMenorQue(entero,namePickle):
		distancias = loadPickle(namePickle)
		parecidos = [x for x in distancias if x< entero ]
		return len(parecidos)


	def imprimeCuantosMenorQue(limite):
		for i in range(limite):
			a = Eval.cuantosMenorQue(i,'DistanciasPickle')

			print(' Niveles con distancia menor que ', i, '   :  ', a )






class MakeClusters():
	todosNiveles = []
	nlevels = 200

	def __init__(self):
		# X = np.array([[1, 2], [1, 4], [1, 0], [4, 2], [4, 4], [4, 0]])
		# clustering = AgglomerativeClustering().fit(X)
		# clustering.labels_

		self.loadLevels()
		# print(self.todosNiveles)
		# self.makeClusters()


	def parteOriginal():

		fileNameI = 'NivelOriginal/Completo' +'.csv'
		nivel = MakeClusters.load1(fileNameI)
		print(nivel[0])
		largoOriginal = len(nivel[0])
		largoNuevo = 28

		for i in range(largoOriginal):
			fileName = 'NivelOriginal/Partes/Nivel_'+ str(i) +'.csv'

			nivel_ = MakeClusters.recorta(nivel,largoNuevo)
			nivel = MakeClusters.recorreUnEspacio(nivel)

			with open(fileName, 'w') as csvfile:
			    writer = csv.writer(csvfile, delimiter=',')
			    writer.writerows(nivel_)





	def distanciaLoseta(uno, dos):
		distancia = 0.0
		enemigos_1 = 0
		enemigos_2 = 0
		saltos_1 = 0
		saltos_2 = 0

		for i in range(len(uno)):
			for j in range(len(uno[0])):
				loseta_1 = uno[i][j]
				loseta_2 = dos[i][j]

				if loseta_1 in [16, 21]:
					if loseta_2 in [0, 5]:
						distancia = distancia + 3
					if loseta_2 in [10, 11, 26, 27, 9]:
						distancia = distancia + 1

				if loseta_1  in [10, 11, 26, 27, 9]:
					if loseta_2  in [0, 5]:
						distancia = distancia + 3
					if loseta_2 in [16, 21]:
						distancia = distancia + 1

				if loseta_1  in [0,5]:
					if loseta_2 not in [0, 5]:
						distancia = distancia + 3


				if loseta_1 == 5:
					enemigos_1 = enemigos_1 + 1
				if loseta_2 == 5:
					enemigos_2 = enemigos_2 + 1

		for j in range(len(uno[0])):
			if uno[-1][j] in [0,5]:
				saltos_1 = saltos_1 +1
		for j in range(len(dos[0])):
			if dos[-1][j] in [0,5]:
				saltos_2 = saltos_2 +1

		distancia = distancia + 3*abs(enemigos_1 - enemigos_2) + 3*abs(saltos_1 - saltos_2)

		return distancia/3


	def distanciaNiveles(nivel_i,nivel_j):

		dista_1 = MakeClusters.distanciaLoseta(nivel_i,nivel_j)

		nivel_i2 = nivel_i[1:]
		nivel_i2.append(nivel_i[0])
		dista_2 = MakeClusters.distanciaLoseta(nivel_i2,nivel_j)

		nivel_i2 = nivel_i[2:]
		nivel_i2.append(nivel_i[0])
		nivel_i2.append(nivel_i[1])
		dista_3 = MakeClusters.distanciaLoseta(nivel_i2,nivel_j)

		nivel_i2 = nivel_i[:-1]
		nivel_i2.insert(0,nivel_i[-1])
		dista_4 = MakeClusters.distanciaLoseta(nivel_i2,nivel_j)

		nivel_i2 = nivel_i[:-2]
		nivel_i2.insert(0,nivel_i[-2])
		nivel_i2.insert(0,nivel_i[-1])
		dista_5 = MakeClusters.distanciaLoseta(nivel_i2,nivel_j)


# recorreUnEspacio 1,2,3 Horizontalmente a la derecha
		nivel_i2 = MakeClusters.recorreUnEspacio(nivel_i)
		dista_6 = MakeClusters.distanciaLoseta(nivel_i2,nivel_j)

		nivel_i2 = MakeClusters.recorreUnEspacio(nivel_i2)
		dista_7 = MakeClusters.distanciaLoseta(nivel_i2,nivel_j)

		nivel_i2 = MakeClusters.recorreUnEspacio(nivel_i2)
		dista_8 = MakeClusters.distanciaLoseta(nivel_i2,nivel_j)


# recorreUnEspacio 1,2,3 Horizontalmente a la izquierda
		nivel_i2 = MakeClusters.recorreUnEspacio(nivel_i,False)
		dista_9 = MakeClusters.distanciaLoseta(nivel_i2,nivel_j)

		nivel_i2 = MakeClusters.recorreUnEspacio(nivel_i2,False)
		dista_10 = MakeClusters.distanciaLoseta(nivel_i2,nivel_j)

		nivel_i2 = MakeClusters.recorreUnEspacio(nivel_i2,False)
		dista_11 = MakeClusters.distanciaLoseta(nivel_i2,nivel_j)



		dista = min(dista_5,dista_4,dista_3,dista_2,dista_1,dista_6, dista_7, dista_8, dista_9, dista_10, dista_11)
		return dista


	def recorreUnEspacio(nivel,direccionDerecha=True):
		nivel_ = nivel.copy()

		if direccionDerecha:
			for i in range(len(nivel)):
				a = nivel[i][0]
				nivel_[i] = nivel[i][1:]
				nivel_[i].append(a)
			return nivel_
		else:
			for i in range(len(nivel)):
				a = nivel[i][-1]
				nivel_[i] = nivel[i][:-1]
				nivel_[i].insert(0,a)
			return nivel_

	def recorta(nivel,tamano):
		nivel_ = nivel.copy()

		for i in range(len(nivel)):
			nivel_[i] = nivel[i][0:tamano]

		return nivel_






	def matrizDistancias(self):
		# self.loadLevels()

		n = len(self.todosNiveles)
		matriz = np.zeros((n,n))

		for i in range(n):
			for j in range(n):
				nivel_i = self.todosNiveles[i]
				nivel_j = self.todosNiveles[j]

				dista = MakeClusters.distanciaNiveles(nivel_i,nivel_j)
				matriz[i,j] = dista
				# matriz[j,i] = dista

		print(matriz)

		self.matrizD = matriz



	def load1(fileName):
		nivel = []
		with open(fileName, 'r') as csvfile:
			lines = csv.reader(csvfile, delimiter=',')

			for row in lines:
				nivelLine = []
	
				for item in row:
					item.strip()
					if item != '':
						nivelLine.append(int(item))
				nivel.append(nivelLine)

		return nivel


# Para filtrar se tiene que haber corrido el método Eval.loadEvaluations().
	def arregloNiveles( fileName, cantidadNiveles, filtrar=False):

		arregloNiveles = []

		for i in range(1,cantidadNiveles+1):
			fileNameI = fileName + '/' + 'Nivel_'+ str(i) +'.csv'

			if not filtrar:
				nivelm = MakeClusters.load1(fileNameI)
				arregloNiveles.append(nivelm)
			else:
				if float(Eval.valueDic[str(i)]) <= -1 :
					nivelm = MakeClusters.load1(fileNameI)
					arregloNiveles.append(nivelm)


		return arregloNiveles



	def loadLevels(self):

		# for i in range(1,self.nlevels+1):
		for i in range(1,3):
			fileName = 'zTextosNiveles/Nivel_'+ str(i) +'.csv'

			nivelm = MakeClusters.load1(fileName)

			self.todosNiveles.append(nivelm)


	def setNiveles(self,matriz):
		self.todosNiveles = matriz





		
	def makeClusters(self):
		# clustering = AgglomerativeClustering().fit(self.todosNiveles)
		# print(clustering.labels_)

		self.otroModulo()

	def otroModulo(self):
		X = np.array( self.todosNiveles)

		# Compute DBSCAN
		db = DBSCAN(eps=0.3, metric='manhattan', min_samples=10).fit(X)
		core_samples_mask = np.zeros_like(db.labels_, dtype=bool)
		core_samples_mask[db.core_sample_indices_] = True
		labels = db.labels_

		# Number of clusters in labels, ignoring noise if present.
		n_clusters_ = len(set(labels)) - (1 if -1 in labels else 0)

		print('Estimated number of clusters: %d' % n_clusters_)
		print("Homogeneity: %0.3f" % metrics.homogeneity_score(labels_true, labels))
		print("Completeness: %0.3f" % metrics.completeness_score(labels_true, labels))
		print("V-measure: %0.3f" % metrics.v_measure_score(labels_true, labels))
		print("Adjusted Rand Index: %0.3f"
		      % metrics.adjusted_rand_score(labels_true, labels))
		print("Adjusted Mutual Information: %0.3f"
		      % metrics.adjusted_mutual_info_score(labels_true, labels))
		print("Silhouette Coefficient: %0.3f"
		      % metrics.silhouette_score(X, labels))

		# # #############################################################################
		# # Plot result

		# # Black removed and is used for noise instead.
		# unique_labels = set(labels)
		# colors = [plt.cm.Spectral(each)
		#           for each in np.linspace(0, 1, len(unique_labels))]
		# for k, col in zip(unique_labels, colors):
		#     if k == -1:
		#         # Black used for noise.
		#         col = [0, 0, 0, 1]

		#     class_member_mask = (labels == k)

		#     xy = X[class_member_mask & core_samples_mask]
		#     plt.plot(xy[:, 0], xy[:, 1], 'o', markerfacecolor=tuple(col),
		#              markeredgecolor='k', markersize=14)

		#     xy = X[class_member_mask & ~core_samples_mask]
		#     plt.plot(xy[:, 0], xy[:, 1], 'o', markerfacecolor=tuple(col),
		#              markeredgecolor='k', markersize=6)

		# plt.title('Estimated number of clusters: %d' % n_clusters_)
		# plt.show()









if __name__ == '__main__':
	main()
